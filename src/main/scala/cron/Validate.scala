package cron

object Validate extends App {


  def validate(session: Int)(f: Int => Int): Int = {
    if (session % 2 == 0) then
      f(session)
    else
      0
  }

  val v =validate(22)
  println(v((x: Int) => x / 2))
}