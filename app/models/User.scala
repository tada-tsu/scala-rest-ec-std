package models

import play.api.libs.json.{JsValue, Json, Writes}
import scalikejdbc._

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
        "password" -> user.password,
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
          password
        )
      }
    }
  }
}