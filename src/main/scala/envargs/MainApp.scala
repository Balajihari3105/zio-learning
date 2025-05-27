package envargs

import zio.{Config, Console, ExitCode, Promise, Scope, UIO, Unsafe, ZIO, ZIOAppArgs, ZIOAppDefault}

import scala.annotation.tailrec
import zio.CanFail.*
import zio.Runtime.default.unsafe

import scala.util.control.Exception.allCatch

object MainApp extends ZIOAppDefault {

  def run = Console.printLine(sys.env("ENV"))


  allCatch
}


object Vishal extends App {

  case class CheckerField(fieldName: String, reportId: Long, transactionId: Option[Long], partyId: Option[Long], position: String, value: Option[String] = None)

  val data = List("Investigating:165~~Domestic~~true,InvestTest:165:471018~~Domestic~~true,Sector:165:471018:1817090~~Banking~~true,amountOfClaimOrDividends:165:471018~~~~false,assetInvolved:165:471018~~~~false,noOfSharesOrUnits:165:471018~~~~false,correspondentBankName:165:471018~~~~false,correspondentBankAddress:165:471018~~~~false,correspondentBankCountryCode:165:471018~~~~false,lastName:165:471018:1817090~~~~false,middleName:165:471018:1817090~~~~false,telephoneNumber:165:471018:1817090~~63095480~~false", "submissionType:165~~A~~false", "telephoneNumber:165:471018:1817090~~987656~~false,submissionType:165~~B~~false")
  val checkerFieldHead = data.head
    .split(",").filter(_.contains("false"))
    .map(_.split("~~").toList).map { x =>
      (x(0).split(":").toList) :+ x(1)
    }.toList flatMap {
    case List(name, reportId, value) => Some(CheckerField(fieldName = name, reportId = reportId.toLong,
      transactionId = None, partyId = None, position = "report", value = Some(value)))
    case List(name, reportId, transactionId, value) => Some(CheckerField(fieldName = name, reportId = reportId.toLong,
      transactionId = Some(transactionId.toLong), partyId = None, position = "transaction", value = Some(value)))
    case List(name, reportId, transactionId, partyId, value) => Some(CheckerField(fieldName = name,
      reportId = reportId.toLong, transactionId = Some(transactionId.toLong), partyId = Some(partyId.toLong), position = "party", value = Some(value)))
    case _ => None
  }
  val checkerFieldsTail = data
    .tail
    .flatMap { row =>
      row.split(",").filter(_.contains("false"))
        .map(_.split("~~").toList)
        .map { x =>
          (x(0).split(":").toList) :+ x(1)
        }.toList flatMap {
        case List(name, reportId, value) => Some(CheckerField(fieldName = name, reportId = reportId.toLong,
          transactionId = None, partyId = None, position = "report", value = Some(value)))
        case List(name, reportId, transactionId, value) => Some(CheckerField(fieldName = name, reportId = reportId.toLong,
          transactionId = Some(transactionId.toLong), partyId = None, position = "transaction", value = Some(value)))
        case List(name, reportId, transactionId, partyId, value) => Some(CheckerField(fieldName = name,
          reportId = reportId.toLong, transactionId = Some(transactionId.toLong), partyId = Some(partyId.toLong), position = "party", value = Some(value)))
        case _ => None
      }
    }

  val checkerDa = data.flatMap(x=>{
    x.split(",").filter(_.contains("false"))
      .map(_.split("~~").toList).map{ x =>
        (x(0).split(":").toList):+ x(1)
      }
  })
//  println(checkerDa)
//  println(checkerFieldHead)
//  println("***")
//  println(checkerFieldsTail)



  /*val result = checkerFieldHead.foldLeft(List.empty[List[CheckerField]]){
   (acc, checker) =>
    val s = checkerFieldsTail.foldLeft(List.empty[List[CheckerField]]){
      (tailAcc, tailChecker ) =>
        if(checker.fieldName == tailChecker.fieldName) then tailAcc +: List(tailChecker)
        else tailAcc +: List(checker,tailChecker)
     }

  }*/
  //
  // val dif =  checkerFieldsTail.diff(checkerFieldHead.map(_.fieldName))
  //
  //  val filerTail = checkerFieldsTail.flatMap(s => checkerFieldHead.filter(_.fieldName == s.fieldName))
  //
  //  val removeTe =  filerTail.flatMap(s => checkerFieldsTail.filterNot(_.fieldName == s.fieldName))

  val matchingHeadFieldsInTail = checkerFieldsTail.flatMap {
    case CheckerField(fieldname, reportId, None, None, "report", _) => checkerFieldHead.filter(x => (x.reportId == reportId) && (x.fieldName == fieldname))
    case CheckerField(fieldname, reportId, Some(trans), None, "transaction", _) => checkerFieldHead.filter(x => (x.transactionId.contains(trans) && (x.fieldName == fieldname)))
    case CheckerField(fieldname, reportId, Some(_), Some(party), "party", _) => checkerFieldHead.filter(x => (x.partyId.contains(party) && (x.fieldName == fieldname)))

  }

  val removingMatchingFieldsFromTail = matchingHeadFieldsInTail.flatMap {
    case CheckerField(fieldname, reportId, None, None, "report", _) => checkerFieldsTail.filterNot(x => (x.reportId == reportId) && (x.fieldName == fieldname))
    case CheckerField(fieldname, reportId, Some(trans), None, "transaction", _) => checkerFieldsTail.filterNot(x => (x.transactionId.contains(trans) && (x.fieldName == fieldname)))
    case CheckerField(fieldname, reportId, Some(_), Some(party), "party", _) => checkerFieldsTail.filterNot(x => (x.partyId.contains(party) && (x.fieldName == fieldname)))
  }


//  println(s"The filteration is " + matchingHeadFieldsInTail)
  //val result = removeTe +:checkerFieldHead
  //rintln(s" The Filter Tail is $filerTail")
  //println(removeTe)
//  println(s"The removed Fields are $removingMatchingFieldsFromTail")
//  //  println(result)
//  val res = removingMatchingFieldsFromTail ::: checkerFieldHead
//  println(s"The res is $res")


  val result = checkerFieldsTail.foldLeft(checkerFieldHead) {
    (headAcc, tailField) =>
      if (headAcc.exists(f =>f.fieldName == tailField.fieldName)) {
        headAcc.filter(f => f.reportId == tailField.reportId && f.position == tailField.position)
      } else tailField +: headAcc
  }

  val totalData = checkerFieldHead.groupBy(_.position)
  println(s" The total data is $totalData")

  val taildata = checkerFieldsTail.groupBy(_.position)
  println(taildata)

val reportHeadData = totalData.getOrElse("report",Nil)
val reportTailData = taildata.getOrElse("report",Nil)


  val result1 = reportTailData.foldLeft(reportHeadData) {
    (headAcc, tailField) =>
      if (headAcc.exists(f => (f.fieldName == tailField.fieldName)&&(f.reportId==tailField.reportId))) {
        headAcc
      } else tailField +: headAcc
  }

  println(result1)

}