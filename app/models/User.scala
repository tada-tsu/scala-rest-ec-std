package models

import enums._
import org.joda.time._
import org.joda.time.format._
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import play.api.Logger
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation.Constraints
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.mvc.AnyContent
import scalikejdbc._
import v1.ECRequest

import scala.concurrent.Future


case class User(
                 id: Int,
                 name: String,
                 screen_name: String,
                 email: String,
                 password: String,
                 admin: Boolean,
                 thumbnail_id: Int,
               ) {
}

object User extends SQLSyntaxSupport[User] {
  override val tableName = "users"


  val u = User.syntax("u")

  implicit val implicitWrites = new Writes[User] {
    def writes(user: User): JsValue = {
      Json.obj(
        "id" -> user.id,
        "name" -> user.name,
        "screen_name" -> user.screen_name,
        "email" -> user.email,
        //        "password" -> user.password,
        "admin" -> user.admin,
        "thumbnail_id" -> user.thumbnail_id,
      )
    }
  }

  def apply(u: ResultName[User])(rs: WrappedResultSet): User = User(
    rs.int(u.id),
    rs.string(u.name),
    rs.string(u.screen_name),
    rs.string(u.email),
    rs.string(u.password),
    rs.boolean(u.admin),
    rs.nullableInt(u.thumbnail_id),
  )

  def all(): List[User] = {
    DB readOnly { implicit session =>
      withSQL {
        select.from(User as u)
      }.map(User(u.resultName)).list.apply()
    }
  }

  def count() = {
    DB readOnly { implicit session: DBSession =>
      withSQL {
        select(sqls.count)
          .from(User as u)
      }.map(rs => rs.long(1)).single.apply().get
    }
  }

  def create(name: String = "no name", screen_name: String, email: String, password: String) = {

    lazy val bcrypt = new BCryptPasswordEncoder()

    DB localTx { implicit session =>
      val u = User.column
      applyUpdate {
        insert.into(User).columns(
          u.name,
          u.screen_name,
          u.email,
          u.password,
        ).values(
          name,
          screen_name,
          email,
          bcrypt.encode(password)
        )
      }
    }
  }

  def findByCredential(login: String): Option[User] = {
    Logger.debug(s"Auth: credential( ${login} ) type(${
      if (login.contains("@")) {
        "email"
      } else {
        "screen_name"
      }
    })")

    findByLoginSQL(login)
  }

  def findByScreenName(screen_name: String): Option[User] = {

    findByLoginSQL(screen_name)
  }

  def findByLoginSQL(login: String): Option[User] = {
    DB readOnly { implicit session =>
      if (login.contains("@")) {
        withSQL {
          select
            .from(User as u)
            .where
            .eq(u.email, login)
        }.map(User(u.resultName)).single.apply()
      } else {
        withSQL {
          select
            .from(User as u)
            .where
            .eq(u.screen_name, login)
        }.map(User(u.resultName)).single.apply()
      }
    }
  }

  def auth(user: User, pass: String) = {
    val bcrypt = new BCryptPasswordEncoder()

    if (bcrypt.matches(pass, user.password)) {

      Logger.info(s"${user.name}<${user.email}> Logged In")

      val token: String = bcrypt.encode(s"${user.screen_name}--${DateTime.now.getMillis() / 1000}")

      val fmt: DateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")
      val expire: String = fmt.print(DateTime.now().plusMonths(6))

      Logger.debug(s"token: token( ${token} ), expire (${expire})")

      LoginToken.create(user.id, token, expire)

      (token, expire)
    } else {
      Tuple1
    }
  }

  def tokenAuth(screen_name: String, token: String, adminAuth:Boolean = false): TokenStatus = {
    val u = User.syntax("u")
    DB readOnly { implicit session =>
      withSQL {
        select(u.result.id, u.result.screen_name, u.result.admin)
          .from(User as u)
          .where
          .eq(u.screen_name, screen_name)
      }.map(UserIdentifications(u.resultName)).single.apply() match {
        case Some(ui: UserIdentifications) => {
          val l = LoginToken.syntax("l")
          withSQL {
            select
              .from(LoginToken as l)
              .where
              .eq(l.user_id, ui.id)
              .and
              .eq(l.token, token)
          }.map(LoginToken(l.resultName)).single.apply() match {
            case Some(lt: LoginToken) => {
              new DateTime(lt.expired_at.toInstant().toEpochMilli(), DateTimeZone.forID(lt.expired_at.getOffset.getId)) match {
                case d if d.isBefore(DateTime.now()) => {
                  Logger.debug("Auth: token expired.")
                  new Expired
                }
                case _ => {
                  adminAuth match {
                    case true => {
                      ui.admin match {
                        case true => {
                          new ValidAdmin
                        }
                        case _ =>{
                          new Invalid
                        }
                      }
                    }
                    case false => {
                      new Valid
                    }
                  }
                }
              }
            }
            case None => {
              new Invalid
            }
          }
        }
        case None => {
          new Invalid
        }
      }
    }
  }

  //  TODO: わからんけどProfile変更とかしたい
  //        どうやるかは知らん
  //
  //  def setProfile(user: User, profile: UserProfile, id: Int): Future[Nothing] = {
  //    val id = user.id
  //    implicit val ec: ExecutionContext = ExecutionContext.Implicits.global
  //
  //    DB futureLocalTx { implicit session =>
  //      Future {
  //        blocking {
  //          update(User).set(
  //            User.column.name -> profile.name,
  //            User.column.screen_name -> profile.screen_name,
  //            User.column.email -> profile.email,
  //          ).where.eq(User.column.id, id)
  //        }.update.apply()
  //
  //      }
  //
  //    }
  //  }


  def register(register: RegisterUser): Option[User] = {
    lazy val bcrypt = new BCryptPasswordEncoder()

    DB localTx { implicit session =>
      val u = User.column
      applyUpdate {
        insert.into(User).columns(
          u.name,
          u.screen_name,
          u.email,
          u.password,
        ).values(
          register.name,
          register.screen_name,
          register.email,
          bcrypt.encode(register.password)
        )
      }

      val us = User.syntax("us")
      withSQL {
        select
          .from(User as us)
          .orderBy(us.id)
          .desc
          .limit(1)
      }.map(User(us.resultName)).single.apply
    }
  }

  def me(request:ECRequest[AnyContent]): Option[User] ={
      request.headers.get("Authorization-User-Screen") match {
        case Some(screen_name) => {
          User.findByScreenName(screen_name)
        }
        case None => {
          None
        }
      }
  }
}

case class UserIdentifications(id: Int, screen_name: String, admin: Boolean)

object UserIdentifications {
  def apply(u: ResultName[User])(rs: WrappedResultSet): UserIdentifications = UserIdentifications(
    rs.int(u.id),
    rs.string(u.screen_name),
    rs.boolean(u.admin),
  )
}

case class UserProfile(name: String, screen_name: String, email: String)

object UserProfile {
  implicit val profileReads: Reads[UserProfile] = (
    (JsPath \ "name").read[String] and
      (JsPath \ "screen_name").read[String] and
      (JsPath \ "email").read[String]
    ) (UserProfile.apply _)
}

case class RegisterUser(name: String, screen_name: String, email: String, password: String)

object RegisterUser {
  val form = Form(
    mapping(
      "name" -> nonEmptyText(maxLength = 32),
      "screen_name" -> nonEmptyText(maxLength = 16),
      "email" -> nonEmptyText.verifying(Constraints.pattern("[\\w\\d_-]+@[\\w\\d_-]+\\.[\\w\\d._-]+".r)),
      "password" -> nonEmptyText(minLength = 8, maxLength = 64),
    )(RegisterUser.apply)(RegisterUser.unapply)
  )
}