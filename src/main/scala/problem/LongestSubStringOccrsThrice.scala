package problem

import scala.annotation.tailrec
import scala.collection.immutable.HashMap
import scala.collection.mutable

object LongestSubStringOccrsThrice extends App {


  def maximumLengthSubString(s: String) = {
    (0 until s.length).flatMap {
        start =>
          val character = s(start)
          s.drop(start)
            .takeWhile(_ == character)
            .zipWithIndex
            .map { case (_, idx) => (character, idx + 1) }
            .groupBy(_._1)
            .map { case (k, v) => k -> v.length }
      }.toList.groupBy(_._1)
      .map((k, v) => k -> v.map(_._2)).filter(_._2.length >= 3)
      .flatMap(_._2)
  }

  val maxSubString =maximumLengthSubString("abscaaa")

  val result = if(maxSubString.isEmpty) -1 else maxSubString.max

  println(result)
}