package jsonEnumFormatExample

object CameCaseConverterForJson {

}

import jsonEnumFormatExample.ExampleFuture.{futureExampel1, futureExampel2}
import play.api.libs.json.*

import java.util.concurrent.CountDownLatch
import scala.concurrent.Await

object JsonUtils {
  def toCamelCase(str: String): String = {
    val parts = str.split("__?") // Split on single or double underscores
    parts.head.toLowerCase + parts.tail.map(_.capitalize).mkString
  }

  // Recursively transform JSON keys
  def transformKeys(json: JsValue, transform: String => String): JsValue = json match {
    case JsObject(fields) =>
      JsObject(fields.map { case (key, value) =>
        transform(key) -> transformKeys(value, transform)
      })
    case JsArray(values) =>
      JsArray(values.map(transformKeys(_, transform)))
    case other => other
  }
}

object Main {
  def main(args: Array[String]): Unit = {
    // Input JSON
    val jsonString = """{"Account_Number__c": 1234, "Name__c": "balaji__c"}"""
    val json = Json.parse(jsonString)

    // Transform to camelCase
    val camelCaseJson = JsonUtils.transformKeys(json, JsonUtils.toCamelCase)
    println("Transformed JSON: " + Json.prettyPrint(camelCaseJson))

    // Parse into case class
    case class Account(accountNumberC: Int, nameC: String)
    implicit val accountReads: Reads[Account] = Json.reads[Account]

    val account = camelCaseJson.validate[Account] match {
      case JsSuccess(acc, _) => println(s"Parsed: $acc"); acc
      case JsError(errors) => println(s"Errors: $errors"); throw new Exception("Validation failed")
    }
  }
}


import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import play.api.libs.json._

class JsonTransformSpec extends AnyFlatSpec with Matchers {
  "JSON transformer" should "convert underscore keys to camelCase" in {
    val input = Json.parse("""{"Account_Number__c": 1234, "Name__c": "balaji"}""")
    val transformed = JsonUtils.transformKeys(input, JsonUtils.toCamelCase)

    val expected = Json.parse("""{"accountNumberC": 1234, "nameC": "balaji"}""")
    transformed shouldBe expected

    case class Account(accountNumberC: Int, nameC: String)
    implicit val reads: Reads[Account] = Json.reads[Account]
    transformed.as[Account] shouldBe Account(1234, "balaji")
  }
}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.DurationInt


object ExampleFuture {

  def futureExampel1() = Future {
    println("fist future1 started..")
    Thread.sleep(2000)
    println("firste futre finesshed...")
  }

  def futureExampel2() = Future {
    println("second future2 started..")
    
    println("seconf futre finesshed...")
  }
}

object FutureExampel extends App {

  val print = Future(println("Hello ! world"))
  val twice = print
    .flatMap(_=>Future(println("Hello !world")))
  
  val x = Option(List(1,2,3))
  val y = Future(List(List(1)))
  val y1=y.map(_.flatMap(y=>y))
  

  //  val latch = new CountDownLatch(1)

  /*  // Handle Ctrl+C (SIGINT)
    sys.addShutdownHook{
      println("Received Ctrl+C, shutting down...")
      latch.countDown() // Release the latch
    }*/
  //  sys.ShutdownHookThread{
  //    println("appliction stoppped")
  //  }


  def call(): Future[Unit] = {
    for {
      _ <- futureExampel1()
      _ = println("intermidate log")
      _ <- futureExampel2()
    } yield ()
  }

  val s = call()

  Await.ready(s, 15.seconds).foreach(_ => println(s"completed"))

  /*s.foreach {
    _ => println("completed")
  }*/

}