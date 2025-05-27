import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder, JsonDecoder, JsonEncoder}

case class Challange(name : String, b:String)

object Challange{
  
  given JsonDecoder[Challange] = DeriveJsonDecoder.gen[Challange]
  given JsonEncoder[Challange] = DeriveJsonEncoder.gen[Challange]
}



