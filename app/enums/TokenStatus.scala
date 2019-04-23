package enums

sealed abstract class TokenStatus
final case class Invalid() extends TokenStatus
final case class Valid() extends TokenStatus
final case class Expired() extends TokenStatus