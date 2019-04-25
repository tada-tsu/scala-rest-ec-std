package models

import play.api.libs.json.{JsValue, Json, Writes}
import scalikejdbc._

case class Setting(
                    id: Int,
                    setting_key: String,
                    value: String,
                  )

object Setting extends SQLSyntaxSupport[Setting] {
  override val tableName = "settings"

  val s = Setting.syntax("s")

  implicit val writes = new Writes[Setting] {
    def writes(s: Setting): JsValue = {
      Json.obj(
        "id" -> s.id,
        "key" -> s.setting_key,
        "value" -> s.value,
      )
    }
  }

  def apply(p: ResultName[Setting])(rs: WrappedResultSet): Setting = Setting(
    rs.int(p.id),
    rs.string(p.setting_key),
    rs.string(p.value),
  )

  def read(key: String) = {
    DB readOnly { implicit session =>
      withSQL {
        select
          .from(Setting as s)
          .where
          .eq(s.setting_key, key)
          .limit(1)
      }.map(Setting(s.resultName)).single.apply
    }
  }

  def reads(key: String) = {
    DB readOnly { implicit session =>
      withSQL {
        select
          .from(Setting as s)
          .where
          .eq(s.setting_key, key)
      }.map(Setting(s.resultName)).list.apply
    }
  }

  def overview() = {
    DB readOnly { implicit session =>
      withSQL {

        select
          .from(Setting as s)
          .where
          .eq(s.setting_key, "logo")
          .or
          .eq(s.setting_key, "store_name")
          .or
          .eq(s.setting_key, "slide")
          .or
          .eq(s.setting_key, "pickups")
          .or
          .eq(s.setting_key, "menu")
          .or
          .eq(s.setting_key, "footer_links")
      }.map(Setting(s.resultName)).list.apply
    }
  }

}



