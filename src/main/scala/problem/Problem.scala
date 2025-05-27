package problem

import java.io.File
import scala.annotation.tailrec
import scala.collection.immutable.{AbstractMap, SeqMap, SortedMap}
import scala.collection.mutable
import scala.io.Source


object Problem {


  def firstOccurence(input: String, expected: String) = {
    /* input.sliding(expected.length)
       .zipWithIndex
       .collectFirst {
         case (str, index) if str.equals(expected) => index
       }.getOrElse(-1)*/

    val file = new File("/Users/bala/IdeaProjects/zio-learning/src/main/resources/application.conf")
    val source = Source.fromFile(file)
    source.foreach(print)

    input.indexOf(expected)
  }

  def searchInsert(nums: Array[Int], target: Int): Int = {

    def search(low: Int, high: Int): Int = {
      if (low == high) low
      else
        val mid = (low + high) / 2
        if (nums(mid) == target) mid
        else if (nums(mid) > target) {
          search(low, mid)
        }
        else search(mid + 1, high)
    }

    search(0, nums.length)
  }

  //        [[0,1],[1,2],[2,3],[3,4]]
  // [[0,4],[4,0],[1,3],[3,0]]
  //[true,false,true,false]


  def checkIfPrerequisite(numCourses: Int, prerequisites: Array[Array[Int]], queries: Array[Array[Int]]) = {



    def prerequisitesAdd(prerequisites: List[List[Int]])(graph: PrerequisitesGraph = PrerequisitesGraph()): PrerequisitesGraph = {
      prerequisites match
        case Nil => graph
        case head :: tail => prerequisitesAdd(tail) {
          head match
            case Nil => graph
            case h :: t =>
              graph.addCourses(h, t)
              graph

        }
    }
    val prerequisitesGraph =  prerequisitesAdd(prerequisites.map(_.toList).toList)(PrerequisitesGraph()).graph


    def checkCoursesAndValid(prerequisitesGraph: mutable.Map[Int,List[Int]], src: Int, target:Int, visitedMap : mutable.Map[Int,Boolean]= mutable.Map.empty[Int,Boolean]):Boolean={
      visitedMap.put(src, true)

      if(src == target) true
      else  {
        prerequisitesGraph.get(src).fold(false){
          s =>
            (for {
              adj <- s
              c = if(visitedMap.get(adj).exists(!_) || !visitedMap.contains(adj)) checkCoursesAndValid(prerequisitesGraph, adj, target, visitedMap) else false
            } yield c).reduceLeft(_ || _)
        }
      }
    }


    queries.map(c => checkCoursesAndValid(prerequisitesGraph, src = c.head, target = c.last)).toList

  }


  case class PrerequisitesGraph(
                                 graph: scala.collection.mutable.Map[Int, List[Int]] = mutable.Map.empty[Int, List[Int]]
                               ) {

    def addCourses(preRequiste: Int, course: List[Int]) = {
      if (graph.contains(preRequiste)) {
        graph.put(preRequiste, course ++ graph(preRequiste))
      } else
        graph.put(preRequiste, course)
    }
  }
}





























