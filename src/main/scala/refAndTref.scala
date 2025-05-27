import zio.stm.TRef
import zio.{Ref, Scope, ZIO, ZIOAppArgs, ZIOAppDefault}
import zio.Console.printLine

object refAndTref extends ZIOAppDefault {

  def updateAndCounterTref(
                            counter: TRef[Int],
                            amount: Int,
                            operationName: String,
                            batchSize: Int,
                            batchNumber: Int
                          ) = {
    for {
      _ <- ZIO.foreachParDiscard(batchNumber to batchSize) {
        _ => counter.update(_ + amount).commit
      }
      value <- counter.get.commit
      _ <- printLine(s"After ${batchNumber * batchSize} $operationName: $value").orDie
    } yield ()
  }


  def updateAndCounterRef(
                           counter: Ref[Int],
                           amount: Int,
                           operationName: String,
                           batchSize: Int,
                           batchNumber: Int
                         ) = {
    for {
      _ <- ZIO.foreachParDiscard(batchNumber to batchSize) {
        _ => counter.update(_ + amount)
      }
      value <- counter.get
      _ <- printLine(s"After ${batchNumber * batchSize} $operationName: $value").orDie
    } yield ()
  }

  override def run = {

    /*  for{
        counter <- TRef.make(0).commit
        inc <- ZIO.foreachPar(1 to 10){
          i => updateAndCounterTref(counter, 1, "increments", 100, i)
        }.fork
  /*      dec <- ZIO.foreachPar(1 to 10){
          i => updateAndCounterTref(counter, -1, "decrement", 100, i)
        }.fork*/

        _ <- inc.join/*.zipPar(dec.join)*/
        res <- counter.get.commit
        _ <- printLine(s"the end result $res")
      }yield()*/

    for {
      counter <- Ref.make(0)
      inc <- ZIO.foreachPar(1 to 5) {
        i => updateAndCounterRef(counter, 1, "increments", 100, i)
      }.fork

      /*dec <- ZIO.foreachPar(1 to 10) {
        i => updateAndCounterRef(counter, -1, "decrement", 100, i)
      }.fork
*/
      _ <- inc.join /*.zipPar(dec.join)*/
      res <- counter.get
      _ <- printLine(s"the end result $res")
    } yield ()
  }

}
