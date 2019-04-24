package seeders

import models.User
import scalikejdbc.config.DBs

object UsersTableSeeder extends App {
  DBs.setup('default)
  for (x <- (1 to 10)) {
    val status = User.create(
      name = s"sample user ${x}",
      screen_name = s"sample_user_${x}",
      email = s"sample${x}@sample.com",
      password = "pass"
    )

    if (status == 1) {
      println(s"Created sample user")
    }
  }
}