package models

import scalikejdbc._

class Overview extends SQLSyntaxSupport[Overview] {
  override val tableName = "settings"
}
