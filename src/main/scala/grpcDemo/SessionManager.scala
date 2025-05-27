package grpcDemo

/*

import play.libs.Json

import scala.reflect.runtime.universe.*

object CaseClass {


  def toMap[T <: Product](caseClass: T, excludedParams: Set[String] = Set.empty)(implicit tag: TypeTag[T]) = {

    val tpe = typeOf[T]
    val fieldNames = tpe.decls.collectFirst { case m: MethodSymbol if m.isPrimaryConstructor => m }
      .map(_.paramLists.flatten.map(_.name.toString))
      .getOrElse(Nil)


    fieldNames
      .zip(caseClass.productIterator.toList)
      .toMap
      .filterNot { case (param, _) => excludedParams.contains(param) }
  }
}


import scala.reflect.runtime.universe._
object Meta extends App {

  case class LeadGenInfo(
                          id: String,
                          adId: Option[String],
                          adgroupId: Option[String],
                          formId: String,
                          leadgenId: String,
                          pageId: String,
                          createdTime: Long,
                          fieldData: Seq[Map[String, Seq[String]]]
                        )

  val lead = LeadGenInfo("1234", Some("adhrebf"), Some("sdvdshvbhs"),
    formId = "trvvcgncn",
    leadgenId = "scdgvvshc",
    pageId = "123435t8yy",
    createdTime = 25637L,
    fieldData = Seq(Map("name" -> Seq("123"))))

  //  val s = CaseClass.toMap (lead, Set("id", "adId"))
  case class Person(i: Int, str: String, i1: Int)

  import grpcDemo.Meta._
  val person = Person(1, "Alice", 25)
  // Convert to map, no exclusions
  val map1 = CaseClass.toMap(person,Set.empty)
  println(map1) // Map(id -> 1, name -> Alice, age -> 25)

  // Exclude "age"
  val map2 = CaseClass.toMap(person, Set("age"))
  println(map2) // Map(id -> 1, name -> Alice)

}*/

import play.api.libs.json.JsonConfiguration.Aux
import play.api.libs.json.JsonNaming.SnakeCase
import play.api.libs.json.{Json, JsonConfiguration, JsonNaming, Reads, Writes}

import scala.reflect.runtime.universe.* // Must be imported

case class Person(id: Int, name: String) // Your case class

object Utils {
  def toMap[T <: Product](caseClass: T, excludedParams: Set[String] = Set.empty)(implicit tag: TypeTag[T]): Map[String, Any] = {
    val tpe = typeOf[T]
    val fieldNames = tpe.decls.collectFirst { case m: MethodSymbol if m.isPrimaryConstructor => m }
      .map(_.paramLists.flatten.map(_.name.toString))
      .getOrElse(Nil)

    fieldNames
      .zip(caseClass.productIterator.toList)
      .toMap
      .filterNot { case (param, _) => excludedParams.contains(param) }
  }
}

object Main extends App {


  case class Lead(id: String, fieldId: String)

  object Lead {
    val SnakeToCamel: JsonNaming = new JsonNaming {
      def apply(property: String): String = {
        // Replace double underscores with single, then convert to camelCase
        val normalized = property.replace("__", "_")
        val parts = normalized.split("_")
        parts.head + parts.tail.map(_.capitalize).mkString
      }
    }
  }
//    implicit val jsonConfig: Aux[Json.MacroOptions] = JsonConfiguration(
//      naming = JsonNaming.SnakeCase
//    )
//    implicit val jsonReads: Reads[Lead] = Json.reads[Lead]
//    implicit val jsonWriter: Writes[Lead] = Json.writes[Lead]
//  }


//  val js = Json.parse("""{"id":"1234","field__id": "helloWorld"}""")

//  println(Json.toJson(js.as[Lead]))


  // Import the Utils object to bring `toMap` into scope
}



import play.api.libs.json._
import play.api.libs.functional.syntax._

// Case classes representing the JSON structure
case class UserData(
                     fullName: String,
                     email: String,
                     phoneNumber: String,
                     customField: String
                   )

case class Value(
                  createdTime: Long,
                  leadgenId: String,
                  formId: String,
                  pageId: String,
                  adId: Option[String], // Optional field
                  userData: UserData
                )

case class Change(
                   field: String,
                   value: Value
                 )

case class Entry(
                  id: String,
                  time: Long,
                  changes: Seq[Change]
                )

case class MetaLeadsPayload(
                             entry: Seq[Entry],
                             objectValue: String // 'object' is a reserved keyword in Scala, so we use backticks
                           )

// Implicit Reads for JSON deserialization
object MetaLeadsPayload {
  implicit val config: Aux[Json.MacroOptions] = JsonConfiguration(naming = JsonNaming.SnakeCase)
  implicit val userDataReads: Reads[UserData] = Json.reads[UserData]
 /* implicit val userDataReads: Reads[UserData] = (
    (JsPath \ "full_name").read[String] and
      (JsPath \ "email").read[String] and
      (JsPath \ "phone_number").read[String] and
      (JsPath \ "custom_field").read[String]
    )(UserData.apply _)*/

  implicit val valueReads: Reads[Value] = Json.reads[Value]
 /* implicit val valueReads: Reads[Value] = (
    (JsPath \ "created_time").read[Long] and
      (JsPath \ "leadgen_id").read[String] and
      (JsPath \ "form_id").read[String] and
      (JsPath \ "page_id").read[String] and
      (JsPath \ "ad_id").readNullable[String] and // Optional field
      (JsPath \ "user_data").read[UserData]
    )(Value.apply _)*/

  implicit val changeReads: Reads[Change] = Json.reads[Change]
 /* implicit val changeReads: Reads[Change] = (
    (JsPath \ "field").read[String] and
      (JsPath \ "value").read[Value]
    )(Change.apply _)*/
  implicit val entryReads: Reads[Entry] = Json.reads[Entry]
 /* implicit val entryReads: Reads[Entry] = (
    (JsPath \ "id").read[String] and
      (JsPath \ "time").read[Long] and
      (JsPath \ "changes").read[List[Change]]
    )(Entry.apply _)*/

  implicit val metaLeadsPayloadReads: Reads[MetaLeadsPayload] = (
    (JsPath \ "entry").read[Seq[Entry]] and
      (JsPath \ "object").read[String]
    )(MetaLeadsPayload.apply _)

  // Example usage: Parse the JSON string into the case class
  def parseJson(jsonString: String): Either[String, MetaLeadsPayload] = {
    val json = Json.parse(jsonString)
    json.validate[MetaLeadsPayload] match {
      case JsSuccess(payload, _) => Right(payload)
      case JsError(errors) => Left(s"Failed to parse JSON: $errors")
    }
  }
}

// Example JSON string
object MainJson extends App {
  val jsonString = """
  {
    "entry": [
      {
        "id": "123456789",
        "time": 1615488000,
        "changes": [
          {
            "field": "leadgen",
            "value": {
              "created_time": 1615488000,
              "leadgen_id": "987654321",
              "form_id": "456789123",
              "page_id": "123456789",
              "ad_id": "111222333",
              "user_data": {
                "full_name": "John Doe",
                "email": "johndoe@example.com",
                "phone_number": "+1234567890",
                "custom_field": "Interested in Product X"
              }
            }
          }
        ]
      }
    ],
    "object": "page"
  }
  """

  // Parse and print the result
  val result = MetaLeadsPayload.parseJson(jsonString)
  result match {
    case Right(payload) => println(s"Successfully parsed: $payload")
    case Left(error) => println(error)
  }
}

import scala.concurrent.{Future, Await}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.util.{Success, Failure}

object FutureTraverseExample extends App {
  // Simulated async function
  def fetchUserName(userId: Int): Future[String] = Future {
    // Simulate some work without Thread.sleep
    println(s"$userId")
    s"User-$userId"
  }

  val userIds = List(1, 2, 3, 4)

  // Traverse the list to fetch names concurrently
  val futureNames: Future[List[String]] = Future.traverse(userIds)(fetchUserName)

  // Handle the result asynchronously
  futureNames.onComplete {
    case Success(names) => println("Fetched names: " + names.mkString(", "))
    case Failure(exception) => println("Failed: " + exception.getMessage)
  }

  // Wait for the result in a better way (for demo purposes)
//  try {
//    val result = Await.result(futureNames, 5.seconds)
//    println("Main thread result: " + result.mkString(", "))
//  } catch {
//    case e: Exception => println("Error waiting for result: " + e.getMessage)
//  }
}