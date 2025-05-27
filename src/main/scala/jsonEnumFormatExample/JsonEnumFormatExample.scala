package jsonEnumFormatExample


import jsonEnumFormatExample.RoleTypes.QA
import play.api.libs.json.*
object JsonEnumFormatExample extends App{

  case class TestJson(id: Long, role: RoleTypes)

  object TestJson{
    given jsonFOrmater: OFormat[TestJson] = Json.format[TestJson]
  }

  println(Json.toJson(TestJson(1234, QA)))

  val json = """{"id":1234,"role":"QA"}"""

  println(Json.parse(json).as[TestJson])

  println(RoleTypes.valueOf("QA"))
}

enum RoleTypes:
  case Developer, QA, DevOps

object RoleTypes {
  // Define Reads for RoleTypes
  implicit val reads: Reads[RoleTypes] = Reads {
    case JsString(value) =>
      try {
        JsSuccess(RoleTypes.valueOf(value))
      } catch {
        case _: IllegalArgumentException =>
          JsError(s"Invalid value for RoleTypes: $value")
      }
    case _ => JsError("String value expected")
  }

  // Define Writes for RoleTypes
  implicit val writes: Writes[RoleTypes] = Writes { roleType =>
    JsString(roleType.toString)
  }

  // Combine Reads and Writes into Format
  implicit val format: Format[RoleTypes] = Format(reads, writes)
}