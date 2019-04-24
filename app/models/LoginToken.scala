package models

import java.time.ZonedDateTime

import play.api.libs.json._
import scalikejdbc._

case class LoginToken(user_id: Int, token: String, expired_at: ZonedDateTime)


object LoginToken extends SQLSyntaxSupport[LoginToken] {
  override val tableName = "login_tokens"


  val l = LoginToken.syntax("l")

  implicit val implicitWrites = new Writes[LoginToken] {
    def writes(ltoken: LoginToken): JsValue = {
      Json.obj(
        "user_id" -> ltoken.user_id,
        "token" -> ltoken.token,
        "expired_at" -> ltoken.expired_at,
      )
    }
  }

  def apply(ltoken: ResultName[LoginToken])(rs: WrappedResultSet): LoginToken = LoginToken(
    rs.int(ltoken.user_id),
    rs.string(ltoken.token),
    rs.dateTime(ltoken.expired_at)
  )

  def create(user_id: Int, token: String, expired_at:String) = {
    DB localTx { implicit session =>
      val lc = LoginToken.column
      applyUpdate {
        insert
          .into(LoginToken)
          .columns(
            lc.user_id,
            lc.token,
            lc.expired_at,
          )
          .values(
            user_id,
            token,
            expired_at,
          )
      }
    }
  }
}