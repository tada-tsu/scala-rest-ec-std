package seeders

import models.{Product, User}
import org.joda.time.DateTime
import scalikejdbc.config.DBs

object ProductsTableSeeder extends App {
  DBs.setup('default)
  for (x <- (1 to 10)) {
    Product.create(
      s"sample-product${x}-${DateTime.now.getMillis / 1000}",
      s"Sample Product ${x}",
      s"Sample product description ${x}",
      x - 1,
      x * 1000,
      x match {
        case 5 => Some(0)
        case _ => None
      },
    )

    println(s"created sample ${x}")
  }
}