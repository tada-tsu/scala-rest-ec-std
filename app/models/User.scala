package models

import org.joda.time._
import org.joda.time.format._
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import play.api.Logger
import play.api.libs.json._
import scalikejdbc._
import enums._

case class User(
                 id: Int,
                 name: String,
                 screen_name: String,
                 email: String,
                 password: String,
                 thumbnail_id: Int
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

  def tokenAuth(screen_name: String, token: String): TokenStatus = {
    val u = User.syntax("u")
    DB readOnly { implicit session =>
      withSQL {
        select(u.result.id, u.result.screen_name)
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
                  new Valid
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
}

case class UserIdentifications(id: Int, screen_name: String)

object UserIdentifications {
  def apply(u: ResultName[User])(rs: WrappedResultSet): UserIdentifications = UserIdentifications(
    rs.int(u.id),
    rs.string(u.screen_name),
  )
}

case class UserProfile(name: String, screen_name: String, password: String, email: String)

