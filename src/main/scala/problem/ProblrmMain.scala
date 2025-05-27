package problem

object ProblrmMain extends App {

  // numCourses = 5
  // preequites = [[0,1],[1,2],[2,3],[3,4]]
  //quires = [[0,4],[4,0],[1,3],[3,0]]

  val r =Problem.checkIfPrerequisite(5, Array(Array(0,1),Array(1,2),Array(2,3),Array(3,4)) , Array(Array(0,4),Array(4,0),Array(1,3),Array(3,0)))


  val p = Problem.checkIfPrerequisite(2, Array(Array(1,0)), Array(Array(0,1),Array(1,0)))

  println(r)
  println(p)

  val q = Problem.checkIfPrerequisite( 2, Array.empty,Array(Array(1,0),Array(0,1)))
  println(q)

  val pre = Array(Array(0,1),Array(1,2),Array(2,3),Array(3,4))
  val dist  =Array.ofDim[Boolean](5, 5)

  for (Array(from, to) <- pre)
    dist(from)(to) = true

    Integer.MIN_VALUE

  println(dist.map(_.mkString(",")).mkString)
}
