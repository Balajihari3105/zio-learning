package catslearning

import catslearning.Subject.printerName
import zio.stm.{TRef, USTM}
import zio.{IO, Ref, Scope, UIO, ZIO, ZIOAppArgs, ZIOAppDefault}
import zio.stream.ZChannel.MergeDecision.Await

import java.io.File
import java.util
import javax.print.attribute.standard.Media
import javax.sound.sampled.{AudioSystem, FloatControl}
import scala.annotation.tailrec
import scala.concurrent.{Future, Promise}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.io.StdIn
import scala.runtime.Nothing$
import scala.util.{Failure, Success}
import scala.concurrent.duration.DurationInt

class TypeClasses extends Logrithms {

  override type Logrithm = Double

}

trait Logrithms:
  type Logrithm

class ex {
  def fun(s: Logrithms)(p: s.Logrithm): s.Logrithm = ???
}

//import scala.language.dynamics
class Record(elems: (String, Any)*) extends Selectable: //extends Dynamic:
  private val fields = elems.toMap

  def selectDynamic(name: String): Any = fields(name)

object Main extends App {

  type Person = Record {
    val name: String
    val age: Int
  }
  val person = Record(
    "name" -> "John",
    "age" -> 42
  ).asInstanceOf[Person]

  println(s"the anme ${person.name} and ${person.age}")
  type FunDec = Int => String

  import cats.implicits._

  def accel: FunDec = (x: Int) => x.toString

  def fun: FunDec = accel

  println(fun(12))

  def fun2(x: FunDec): Unit = println(x)

  trait Nums {
    type Num
  }

  trait A:
    def children: List[A]

  trait B:
    def children: List[B]

  //  class C extends A, B {
  //    //override def children: List[B] = ???
  //  }

  case class MyIO[A](unsaferun: () => A) {
    def map[B](f: A => B): MyIO[B] = MyIO(() => f(unsaferun()))

    def flatMap[B](f: A => MyIO[B]): MyIO[B] = {
      MyIO(() => f(unsaferun()).unsaferun())
    }
  }

  val clock: MyIO[Long] = MyIO(() => System.currentTimeMillis())

  val totalTime = for {
    startTime <- clock
    endTime <- clock
  } yield (startTime - endTime)


  clock.flatMap(startTime => clock.map(endTime => endTime - startTime))


  val number = MyIO(() => 54)

  val incrementDimensions1 = (x: Long) => x + 5
  val incrementBy5 = clock.map(num => incrementDimensions1(num))


  /*

  case class MyOps(nums : () => List[Int]){

    def map(f: Int => Int)={
      MyOps(()=> f(nums()))
    }
*/


  val l = List("1", "2", "3")

  val users = Map(
    "Alice" -> Map("age" -> 25, "city" -> "NYC"),
    "Bob" -> Map("age" -> 30, "city" -> "SF"),
    "Charlie" -> Map("city" -> "LA")
  )

  def getUserInfo(name: String) = {

    if (users.contains(name) && users(name).contains("age")) {
      s"$name is ${users.get(name).get("age")} years old."
    }
    else "None"
  }

  sealed trait Tree

  case class Node(value: Int, left: Tree, right: Tree) extends Tree

  case object Leaf extends Tree

  val nodes = Node(1, Node(1, Leaf, Leaf), Node(2, Leaf, Leaf))

  /*  def sumAllTheNode(tree: Node, acc :Int = 0): Int={
    tree match
      case Node(value, _: Leaf.type, _: Leaf.type) => acc + value
      case Node(value, x: Node, _: Leaf.type ) => sumAllTheNode(x, acc)
      case Node(value, _ : Leaf.type , rit : Node ) => sumAllTheNode(rit, acc)
      case tree => sumAllTheNode(tree, acc)
  }*/


  //  println(s"the sum of node is : ${sumAllTheNode(nodes)}")
  //price (Double): the original price of the product.
  //discount (Double): the discount percentage to apply to the price (default value: 10%).
  //The higher-order function should accept the following parameters:
  //price (Double): the price of the product.
  //discountFn (Function): the discount function that applies a discount to the price.
  //The function should return the discounted price.


  def priceCalculation(quantity: Double, price: Int): Double = {
    quantity * price
  }

  def discount(price: Double) = {
    price - 0.1
  }

  def discountFn(price: => Double) = {
    println(s"discfun $price")
  }

  def functionDouble(x: Double) = {
    println("hello")
    x % 10
  }

  println(discountFn(functionDouble(10.1)))


  val list = List(1, 5, 79, 9, 10)


  def findSmallest[T](list: List[T], compare: (T, T) => Int): T = {
    list.reduce((a, b) => if (compare(a, b) <= 0) a else b)
  }

  val compare = (x: Int, y: Int) => x - y

  def removeFirstOccurenceOFList[T](list: List[T], ele: T): List[T] = {

    val (before, after) = list.span(s => s != ele)

    before ::: after.drop(1)
  }

  def sort[T](list: List[T], compare: (T, T) => Int): List[T] = {
    def loop(remaining: List[T], acc: List[T]): List[T] = {
      if (remaining.isEmpty) acc
      else {
        val smallest = findSmallest(remaining, compare)
        val removedList = removeFirstOccurenceOFList(remaining, smallest)
        loop(removedList, smallest +: acc)
      }

    }

    loop(list, List())
  }

  println(sort(list, compare))

  val smallest = findSmallest(List(1, 2), compare)
  println(smallest)

  println("balaji".reverse)
}

object CustomSort {
  // Comparison function (like Comparator.compare)
  val compare: (Int, Int) => Int = (a, b) => a - b

  // Find the smallest element in the list using the comparison function
  def findSmallest[T](list: List[T], compare: (T, T) => Int): T = {
    list.reduce((a, b) => if (compare(a, b) <= 0) a else b)
  }

  // Remove the first occurrence of an element from the list
  def removeFirst[T](list: List[T], elem: T): List[T] = {
    val (before, after) = list.span(_ != elem)
    before ++ after.drop(1)
  }

  // Manually construct a sorted list
  def customSort[T](list: List[T], compare: (T, T) => Int): List[T] = {
    def loop(remaining: List[T], acc: List[T]): List[T] = {
      if (remaining.isEmpty) acc
      else {
        val smallest = findSmallest(remaining, compare)
        loop(removeFirst(remaining, smallest), acc :+ smallest)
      }
    }

    loop(list, List())
  }

  def main(args: Array[String]): Unit = {
    val unsorted = List(5, 2, 8, 1, 9, 3)
    val sorted = customSort(unsorted, compare)
    println(s"Unsorted: $unsorted")
    println(s"Sorted: $sorted")
  }
}


object MinOFMax extends App {

  // Comparator function: returns negative if a < b, zero if a == b, positive if a > b
  def compare: (Int, Int) => Int = (a, b) => a - b

  // Find the minimum element in a list using a comparator
  def min[A](li: List[A], compare: (A, A) => Int): Option[A] = {
    li.foldLeft(Option.empty[A]) { (acc, curr) =>
      acc match {
        case None => Some(curr)
        case Some(min) => Some(if (compare(min, curr) <= 0) min else curr)
      }
    }
  }

  // Find the maximum element in a list using a comparator
  def max[A](li: List[A], compare: (A, A) => Int): Option[A] = {
    li.foldLeft(Option.empty[A]) { (acc, curr) =>
      acc match {
        case None => Some(curr)
        case Some(max) => Some(if (compare(max, curr) >= 0) max else curr)
      }
    }
  }

  // Find the list of maximums of minimums from a list of lists
  def maxOfList[A](inputList: List[List[A]], compare: (A, A) => Int): Option[List[A]] = {

    def loop(list: List[List[A]], acc: List[Option[A]]): List[A] = {
      list match
        case Nil =>
          acc.flatten
        case head :: tail =>
          val smallest = min(head, compare)
          println(s"inside the loop")
          loop(tail, smallest +: acc)
    }

    val s = loop(inputList, List.empty)
    println(s)

    if (inputList.isEmpty) None
    else {
      val minList = inputList.flatMap(sublist => min(sublist, compare))
      max(minList, compare).map(List(_))
    }
  }

  // Example usage
  val list = List(List(1, 2, 3), List(2, 4, 5, 6), List(10, 11))
  println(maxOfList(list, compare)) // Output: Some(List(10))

  println(min(List(1, 2, 3), compare))

  println(list.map(_.min).max)

}

object Segerate extends App {

  val s = List("a", "b", "c", "a")
  println(s.groupBy(t => t))

  println(s.sorted)

  println(s.sortWith(_ < _))

  println(s.sortBy(_.max))

}

object MainOFminMax extends App {

  def compare: (Int, Int) => Int = (a, b) => a - b

  def min[A](li: List[A], minimum: List[A], compare: (A, A) => Int): A = {
    li match {
      case Nil => minimum.head
      case head :: tail =>
        if (minimum.isEmpty) min(tail, List(head), compare)
        else {
          val smallest = if (compare(minimum.head, head) >= 0) minimum else List(head)
          min(tail, smallest, compare)
        }
    }
  }

  def max[A](li: List[A], maximium: List[A], compare: (A, A) => Int): A = {
    li match {
      case Nil => maximium.head
      case head :: tail =>
        if (maximium.isEmpty) min(tail, List(head), compare)
        else {
          val biggest = if (compare(maximium.head, head) <= 0) maximium else List(head)
          min(tail, biggest, compare)
        }
    }
  }

  def maxOfList[A](inputList: List[List[A]], compare: (A, A) => Int): Option[List[A]] = {

    def loopMax(li: List[List[A]], acc: List[A]): List[A] = {

      li match {
        case Nil => acc
        case head :: tail => {
          val biggest = max(head, List.empty, compare)
          loopMax(tail, biggest +: acc)
        }
      }
    }

    if (inputList.isEmpty) None
    else {
      val minList = loopMax(inputList, List.empty)

      Some(minList)
    }
  }

  val list = List(List(1, 2, 3), List(2, 4, 5, 6), List(10, 11))
  println(maxOfList(list, compare))

  def zipIndex[A](list: List[A], map: Map[Int, A] = Map.empty, index: Int = 0): Map[Int, A] = {
    list match {
      case Nil => map
      case head :: tail => zipIndex(tail, map.updated(index, head), index + 1)
    }
  }

  def findIndexByElement[A](list: List[A], tragetIndex: Int): Option[A] = {
    val listWithIndex = zipIndex(list)
    listWithIndex.get(tragetIndex)
  }

  def findElementByIndex[A](ints: List[A], ele: A): Option[Int] = {
    val zipByIndex = zipIndex(ints)
    zipByIndex.foldLeft(Option.empty[Int]) {
      case (acc, (k, v)) =>
        if (v == ele)
          Some(k)
        else acc

    }
  }

  val listElemetn = List(1, 2, 4, 5)
  println(findElementByIndex(listElemetn, 3))
  println(findElementByIndex(listElemetn, 2))
}

object Formatter extends App {
  trait JsValue

  case class Json(value: String) extends JsValue

  trait JsonFormatter[A]

  trait JsonSerialisation[A] extends JsonFormatter[A] {
    def toJson(value: A): JsValue
  }

  given JsonSerialisation[Person] with {
    def toJson(value: Person): JsValue = {
      Json(s"""{"value": "${value.name}"}""")
    }
  }


  extension [A](value: A)(using jsonFormatter: JsonSerialisation[A]) {
    def toJson: JsValue = jsonFormatter.toJson(value)

  }

  case class Person(name: String)

  val s = Person("balaji").toJson

  println(s"$s")


}


object CountAndSayExample extends App {
  /*def countAndSay(n: Int): String = {


    def countTheFunc(str: String, ch: Char, acc: String = ""): String = {
      val seqCount = str.takeWhile(_ == ch).length
      val remaining = str.dropWhile(_ == ch)
      if (remaining.nonEmpty)
        countTheFunc(remaining, remaining.head, acc+ s"$seqCount$ch")
      else acc+ s"$seqCount$ch"

    }

    def loop(n: Int, increment: Int = 1, acc: String=""): String = {
      if (increment > n)
        acc
      else if (increment == 1) loop(n, increment + 1, "1")
      else
        loop(n, increment+1 ,  countTheFunc(acc, acc.head))
    }

    loop(n)

  }
*/


  def countAndSay(n: Int): String = {
    if (n == 1) "1"
    else runLengthEncoding(countAndSay(n - 1))

  }


  def runLengthEncoding(str: String, acc: String = ""): String = {
    if (str.nonEmpty) {
      val seqCount = str.takeWhile(_ == str.head).length
      val remaining = str.dropWhile(_ == str.head)
      if (remaining.nonEmpty)
        runLengthEncoding(remaining.tail, acc + s"$seqCount${str.head}")
      else runLengthEncoding(remaining, acc + s"$seqCount$str")
    }
    else acc
  }

  println(countAndSay(3))
}


case class Subject(private val name: String) {
  private def printName(): Unit = {
    println(name)
  }
}

object Subject {
  def printerName(subject: Subject) = {
    println(subject.name)
    subject.printName()
  }
}


object ObjectExampel extends App {
  val subject = Subject("balaji")

  //  printerName(subject)


  val str = "balaji"

  println(str(1))

  println(str.indexOf("l"))

  case class Person(name: String)

  val person = Person("balaji")

  person match
    case Person(name) => println(s"the name is $name")
    case _ => println("Invalid")


      val list: List[Nothing] = Nil

  trait A {
    def message: String = "A"
  }

  trait B extends A {
    override def message: String = s"B -> ${super.message}"
  }

  trait C extends A {
    override def message: String = s"C -> ${super.message}"
  }

  class D extends B with C

  val d = new D
  println(d.message) // C -> B -> A

  val promise = Promise[Int]()

  Thread.sleep(1000)

  def promiseFn() = {
    promise.success(21)
    promise.future
  }

  promiseFn().onComplete {
    case Success(value) => println(value)
    case Failure(exception) => println(exception.toString)
  }

  def func(x: Int, y: Int) = {
    println(x)
  }

  val t = func(10, _)
  t(2)


}


object VarianCeExampel {

  def main(args: Array[String]): Unit = {
    println("Hello, World!")

    trait Animal
    case class Dog(name: String) extends Animal
    case class Cat(name: String) extends Animal

    case class Cage[A](animal: A)


    val dog = Dog("gold")
    val cat = Cat("black")

    val cageDog: Cage[Dog] = Cage[Dog](dog)

    println(cageDog)

    // 	val cageAnimal: Cage[Animal] = Cage[Dog](dog)

    // 	println(cageAnimal)


    //covariant

    case class AnimalContainer[+A](animal: A)


    val animalCage: AnimalContainer[Animal] = AnimalContainer[Dog](dog)

    println(animalCage)

    // val animalCageDog : AnimalContainer[Dog] = AnimalContainer[Animal](dog)

    // println(animalCageDog)


    // contravariance

    val animalDog: Animal = dog

    case class AnimalCageContainer[-A]()
    val cageAminals: AnimalCageContainer[Dog] = AnimalCageContainer[Animal]()

    println(cageAminals)

    val s: List[Int] = List(1, 2, 4, 6, 8)


    println(s.withFilter(s => s % 2 == 0).foreach(println))
    val result = for {
      n <- s.withFilter(_ % 2 == 0) // Filter even numbers
      _ = println(n)
      m <- List(n * 2) // Double the filtered numbers
    } yield m


    /* println(result)

     println("enter")
     val str = StdIn.readLine()
     val exceptation = StdIn.readLine()
     println("enter")*/

    def subString(str: String) = {
      def loop(st: String, acc: List[String]): List[String] = {
        if (st.isEmpty) {
          acc
        }
        else {
          loop(st.tail, st +: acc)
        }
      }

      loop(str, List.empty[String])
    }

    /*val possibleSubString = subString(str)

    println(possibleSubString)*/
    //    println(possibleSubString.contains(exceptation))

    def fun: (Int, Int, Int) = {
      (10, 22, 3)
    }

    println(fun._1)


    println("the facrt")


    class MyString(str: String) {
      def reverse() = str.reverse

    }

    implicit def myString(str: String): MyString = new MyString(str)

    val str: MyString = "hello world"

    println(str.reverse())

    case class PI(value: Double)
    implicit val piConst: PI = PI(3.14)
    case class Circle(radius: Double) {

      def calculateRadius(implicit pi: PI) = {
        2 * pi.value * radius * radius
      }
    }

    implicit def circleConversion(radius: Double): Circle = Circle(radius)

    val circle: Circle = 2.0

    println(circle.calculateRadius)

    /* def reverseOfAString(str: String) = {
       str.foldRight(Option.empty[String]) {
         (st, acc) =>
           if (acc.isEmpty) Some(st.toString)
           else acc.map(s =>   s + st)
       }
     }*/
    def reverseOfAString(str: String): String = {
      if (str.isEmpty || str.length == 1) str
      else str.last + reverseOfAString(str.substring(1, str.length - 1)) + str.head
    }

    println(reverseOfAString("balaii"))

    val baa = "balaji"

    //  println(baa.last == baa.head )
    def subString1(str: String) = {
      str.foldLeft(List.empty[String]) {
        (acc, st) => str.dropRight(str.indexOf(st)) +: acc
      }
    }

    println(baa.substring(baa.length - 1))

    trait Compare[A] {
      def compare(x: A, y: A): Int
    }
    given comapar: Compare[String] with {
      override def compare(x: String, y: String): Int = x.compareTo(y)
    }


    extension (x: String)(using compare: Compare[String]) {
      def -(y: String): Int = compare.compare(x, y)
    }
    val com: Int = "b" - "a"
    println(com)
  }


}

object mix extends App {

  def min[A](li: List[A], compare: (A, A) => Int): Option[A] = {
    li.foldLeft(Option.empty[A]) {
      (acc, ele) =>
        acc match
          case Some(value) => Some(if (compare(value, ele) > 0) ele else value)
          case None => Some(ele)
    }
  }


  def max[A](list: List[A], compare: (A, A) => Int): Option[A] = {
    list.foldLeft(Option.empty[A]) {
      (acc, ele) =>
        acc match
          case Some(value) => Some(if (compare(value, ele) < 0) ele else value)
          case None => Some(ele)
    }
  }


  val compare: (Int, Int) => Int = (a, b) => a - b

  def maxOFmix[A](li: List[List[A]], compare: (A, A) => Int): Option[A] = {
    // def loop(list: List[List[A]], acc: List[Option[A]]): List[A] = {
    //      list match
    //        case Nil =>
    //          acc.flatten
    //        case head :: tail =>
    //          val smallest = min(head, compare)
    //          println(s"inside the loop")
    //          loop(tail, smallest +: acc)
    //    }
    def findMin(lis: List[List[A]], acc: List[Option[A]] = List.empty): List[Option[A]] = {
      li match {
        case Nil => acc
        case head :: tail =>
          val smallest = min(head, compare)
          findMin(tail, smallest +: acc)
      }
    }

    val listOfMin = findMin(li)
    println(s"list of min $listOfMin")
    None
  }

  val list = List(List(1, 2, 3), List(9, 8, 4))
  maxOFmix(list, compare)
  //  val m = max(List(9,8,4), compare)
  //  println(maxOFmix(list, compare))


}


object Exap extends App {
  // Comparator function: returns negative if a < b, zero if a == b, positive if a > b
  def compare: (Int, Int) => Int = (a, b) => a - b

  // Find the minimum element in a list using a comparator
  def min[A](li: List[A], compare: (A, A) => Int): Option[A] = {
    li.foldLeft(Option.empty[A]) { (acc, curr) =>
      acc match {
        case None => Some(curr)
        case Some(min) => Some(if (compare(min, curr) <= 0) min else curr)
      }
    }
  }

  // Find the maximum element in a list using a comparator
  def max[A](li: List[A], compare: (A, A) => Int): Option[A] = {
    li.foldLeft(Option.empty[A]) { (acc, curr) =>
      acc match {
        case None => Some(curr)
        case Some(max) => Some(if (compare(max, curr) >= 0) max else curr)
      }
    }
  }

  // Find the list of maximums of minimums from a list of lists
  def maxOfList[A](inputList: List[List[A]], compare: (A, A) => Int) = {

    def loop(list: List[List[A]], acc: List[Option[A]]): List[A] = {
      list match
        case Nil =>
          acc.flatten
        case head :: tail =>
          val smallest = min(head, compare)
          println(s"inside the loop")
          loop(tail, smallest +: acc)
    }


    //    println(s"the mini $s")


    if (inputList.isEmpty) None
    else {
      val minList = loop(inputList, List.empty) //inputList.flatMap(sublist => min(sublist, compare))
      max(minList, compare)
    }
  }


  val list = List(List(1, 2, 3), List(9, 8, 4))
  println(maxOfList(list, compare))

  import concurrent.Future
  import scala.concurrent.ExecutionContext.Implicits.global

  def func(): Future[Int] = {
    if (0 == 0) Future.failed(Throwable("null pointer exception"))
    else Future.successful(21)
  }.recoverWith {
    case ex: Throwable => Future.successful(23)
  }
}

//implicit scala.concurrent.ExecutionContext.Implicits.global
//import scala.concurrent.Future


object SecondMaxNumberInList extends App {
  val list = List(1, 34, 6, 7, 9)

  def findMaximumOption(list: List[Int]) = {
    if (list.isEmpty) None
    else {
      val (mx, smx) = list.foldLeft((Option.empty[Int], Option.empty[Int])) {
        case (acc, ele) =>
          acc match
            case (None, None) => (Some(ele), None)
            case (mx, None) if (mx.exists(_ > ele)) => (mx, Some(ele))
            case (mx, None) if (mx.exists(_ < ele)) => (Some(ele), mx)
            case (mx, min) if (mx.exists(_ < ele)) => (Some(ele), mx)
            case (mx, min) if (mx.exists(_ > ele) && min.exists(_ < ele)) => (mx, Some(ele))
            case mxAndSmax => mxAndSmax
      }
      smx
    }
  }


  println(s"the scond Maximum number is ${findMaximumOption(List.empty)}")

  println(list.sorted.reverse.lift(1))
  println(list.distinct.filterNot(_ == list.max).max)

  println(list.partition(_ == list.max))

  println(list.scanLeft(Int.MinValue) {
    (acc, ele) => if (acc > ele) acc else ele
  })

}


object FindSubString extends App {
  val str = "abcdef"

  val tr = str
    .toList
    .tails
    .toList
  //    map(_.mkString)

  println(tr.toList)

  def function(x: Int) = {
    println(x)
    if (x != 0) Future.successful(x * 2)
    else Future.failed(new RuntimeException("non zero exception"))
  }

  val list = List(1, 2, 3, 5, 6)

  //  val seq = Future.sequence()
  //  val s =  Future.traverse(list) { x => function(x) }

  val fistr = list.map(function)

  val st = Future(23)

  /*

    val firstCompleted = Future.firstCompletedOf(fistr)
    for {
      s <- firstCompleted
      _ <- Future.successful(  println(s"first completed ${s/2}"))
    } yield ()
  */


  val dock = Future.foldLeft(fistr)(List.empty[Int]) {
    (acc, res) => (res * 10) +: acc
  }


  val reduce = Future.reduceLeft(fistr) {
    (x, y) => (x + y) * 10
  }


  dock.onComplete {
    case Failure(exception) => println(s"the exception ${exception.getMessage}")
    case Success(value) => println(s"result $value")
  }

  val pr = Promise[Int]

  def funPromise(x: Int, promise: Promise[Int]) = {
    Thread.sleep(2000)
    if (x == 0) promise.failure(new RuntimeException("x is zeero"))
    else promise.success(x)
  }

  val promiseValideate = funPromise(10, pr).future

  promiseValideate.onComplete {
    case Failure(exception) => println(s"the exception ${exception.getMessage}")
    case Success(value) => println(s"result $value")
  }


  val lis = List(
    "Scala is great. Scala is scalable!",
    "Functional programming in Scala is powerful."
  )


  val num = List(1, 2, 5, 8, 9, 3, 5)

  val c = lis.flatMap(s => s.toLowerCase.replaceAll("[^a-z]\\s+", "").split("\\s+").toList)
    .filter(_.nonEmpty)
    .groupBy(identity)
    .map {
      case (k, v) => (k, v.size)
    }
    .toList

  println(c)

}


object CountTheWordInParageaph extends App {

  val lis = List(
    "Scala is great. Scala is scalable!",
    "Functional programming in Scala is powerful."
  )


  val c = lis.flatMap(s => s.toLowerCase.replaceAll("[^a-z]\\s+", "").split("\\s+").toList)
    .filter(_.nonEmpty)
    .groupBy(identity)
    .map {
      case (k, v) => (k, v.size)
    }
    .toList
    .sortWith { case (x, y) => x._2 < y._2 }


  println(c)


  val ms = Map(1 -> 2).view.mapValues(_ * 2).toMap
  val num = List(1, 2, 5, 8, 9, 3, 5)

  //  println(num.sorted)
  //  println(num.sortBy(x => -x))
  //  println(num.sortWith { case (x, y) => x > y })
  //  "".permutations


  val freqMap = num.groupBy(identity).view.mapValues(_.size)

  // Step 2: Sort by frequency (descending) and then by number (ascending)

  println(freqMap.toList.sortBy { case (x, y) => (-y, x) }.map(_._1))

  val s = new Fun:
    override val name: String = ???


}


abstract class AppConfig {
  val name: String = "balaji"

  def nameF: String
}

trait Fun {
  val name: String
  //   def m(): String
}

class A extends Fun {
  override val name: String = "balaji"
}


object MappF extends App {


  trait Functor[F[_]] {
    def map[A](x: A)(f: A => F[A]): F[A] = {
      f(x)
    }
  }

  //  trait Monad[M[_]] extends Functor [M]{
  //    def flatMap[A](x: M[M[A]])(f: M[A] => M[A]): M[A] = {
  //
  //    }
  //  }
  def funF[F[_]](name: => F[Int]): F[Int] = {
    name
  }

  println(funF(List[Int](2)))

  //  object A extends Monad[List]{
  //    flatMap(List(List(1,2,3)))(x=> x.map(s =>s*2))
  //  }
}


object T {

  import scala.concurrent.{Future, Await}
  import scala.concurrent.ExecutionContext.Implicits.global
  import scala.concurrent.duration.DurationInt

  object HelloWorld {

    val listOfString = List("dog", "Lion", "Tiger", "Duck", "Elephant")

    def lookUp(n: Int): Future[Option[String]] = {
      if (n >= 0 && listOfString.nonEmpty && n < listOfString.size)
        Future.successful(Some(listOfString(n)))
      else Future.successful(None)
    }


    //concat listOfint

    //TigerElephant


    def concatElements(inputList: List[Int]): Future[String] = {
      /*inputList.foldLeft(""){
        (acc, index) =>
          lookUp(index).map{ eleOpt =>  match{
            case Some(element) => acc + element
            case _ => acc
          }
        }*/

      Future
        .traverse(inputList)(index => lookUp(index))
        .map { li =>
          li.foldLeft("") { (acc, eleOpt) =>
            eleOpt match {
              case Some(ele) => acc + ele
              case None => acc
            }
          }
        }
    }

    def main(args: Array[String]): Unit = {

      // 	println(lookUp(7))

      val listOfIndex = List(1, 2, -1, 8)

      // 	val concatResult = concatElements(listOfIndex)

      // for{
      //   concatResult <- concatElements(listOfIndex)
      //   _ <- Future.successful(println(concatResult))
      // } yield ()

      val concatResult = Await.result(concatElements(listOfIndex), 2.second)
      println(concatResult)
    }


  }
}

type Name = String

opaque type Codino = Int
/*

object Codino {
  def apply(x: Int): Codino = {
    x
  }
}
*/

object LearingSome extends ZIOAppDefault {

  // ZIO[Any, Throwable, Int] => Task[Int]
  /*  object Integer {
      def apply(x: String): Integer = {
        x
      }
    }*/

  enum DynamicException:
    case InvalidNum, Ivaie

  println(DynamicException)

  def account(n: Int): IO[DynamicException, Unit] = {
    if (n % 2 == 0) then ZIO.unit else ZIO.fail(DynamicException.InvalidNum)
  }

  import zio.Console.printLine
  import zio.Duration
  def discardSleep() = {
    val path = "/Users/bala/Desktop/untitled/Rasaali.mp3"
    for {
      _             <- printLine("playing sound..")
      file          <- ZIO.succeed(File(path))
      audioStream   <- ZIO.attempt(AudioSystem.getAudioInputStream(file))
      clip          <- ZIO.attempt(AudioSystem.getClip())
      _ <- ZIO.attempt{
        clip.open(audioStream)
        val floatGain = clip.getControl(FloatControl.Type.MASTER_GAIN).asInstanceOf[FloatControl]
        floatGain.setValue(1)
        clip.start()
      }
      _ <- ZIO.sleep(Duration.fromSeconds(2))
    } yield ()
  }


  def optionHandle={
    val s = if(10 %2==0) Some(2) else None
    ZIO.fromOption(s)
  }.orElseFail("noe exception")

  def refAct()={
    for{
      r <- Ref.make(0)
      _ <- r.set(2)
      v <- r.get
      _<- printLine(s"the set value $v")
      _ <- r.update(_+10)
      u <- r.get
      _ <- printLine(s"updated value $u")
      m <- r.modify(s => (1000, s+10))
      mv <- r.get
      _ <- printLine(s"the modify value $m")
      _ <- printLine(s"the modifu u value $mv")
    } yield ()
  }


  def function(value: Int)={
    value + 1
  }


  case class BankAccount(balance: USTM[zio.stm.TRef[Int]])


  val b = BankAccount(TRef.make(100))

  val s = b.balance.commit
  def comps()={
    val s = new AnyRef()
    for{
//      s <- TRef.make(0)
      sr <- Ref.make(0)
      uv <- ZIO.foreachPar(1 to 5){
        _ => ZIO.foreachPar(1 to 100){
          _ => sr.update(function)
        }.fork
      }
      j <- ZIO.foreachPar(uv){
        _.join
      }
//     _ <- ZIO.sleep(Duration.fromSeconds(2))
      res <-  sr.get
      _ <- printLine(s"the result is of the system $res")
    } yield ()
  }
  override def run = {
    account(10) *> comps() *> refAct() /**> discardSleep()*/ *> ZIO.never
  }.catchAllCause(ex => zio.Console.printLine(s"the halder $ex"))
}