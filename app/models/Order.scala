package models

import play.api.libs.json.{JsValue, Json, Writes}
import scalikejdbc._
case class Order(
                    id: Int,
                    user_id: Int,
                    status: String,
                    amount: Int,
                  )

object Order extends SQLSyntaxSupport[Order] {
  override val tableName = "orders"
  val o = Order.syntax("o")

  implicit val writes = new Writes[Order] {
    def writes(o: Order): JsValue = {
      Json.obj(
        "id" -> o.id,
        "user_id" -> o.user_id,
        "status" -> o.status,
        "amount" -> o.amount,
      )
    }
  }

  def apply(r: ResultName[Order])(rs: WrappedResultSet): Order = Order(
    rs.int(r.id),
    rs.int(r.user_id),
    rs.string(r.status),
    rs.int(r.amount),
  )


}