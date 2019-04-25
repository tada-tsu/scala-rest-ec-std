package seeders

import models.Setting
import play.api.libs.json.Json
import scalikejdbc._
import scalikejdbc.config.DBs

object SettingsTableSeeder extends App {
  DBs.setupAll()
  DB localTx {
    implicit session =>
      val c = Setting.column
      applyUpdate {
        insert
          .into(Setting)
          .columns(
            c.setting_key,
            c.value,
          )
          .multipleValues(
            // Logo
            Seq("logo", "/logo.png"),
            // Store Name
            Seq("store_name", "sample_store"),
            // Slides
            Seq("slide", Json.toJson(Json.obj(
              "src" -> "sample.png",
              "url" -> "sample.com",
              "caption" -> "sample slide 1",
            )).toString()),
            Seq("slide", Json.toJson(Json.obj(
              "src" -> "sample.png",
              "url" -> "sample.com",
              "caption" -> "sample slide 2",
            )).toString()),
            Seq("slide", Json.toJson(Json.obj(
              "src" -> "sample.png",
              "url" -> "sample.com",
              "caption" -> "sample slide 3",
            )).toString()),


            // Pick up
            Seq("pickups", "1,2,3,4,4,5"),
            // Menu
            Seq("menu", None),
            // Footer links
            Seq("footer_links", "samplelink"),
          )

      }
  }
}
