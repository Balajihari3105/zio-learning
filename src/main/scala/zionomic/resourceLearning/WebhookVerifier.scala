import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import java.nio.charset.StandardCharsets

object WebhookSignatureGenerator {
  def generateSha256Signature(payload: String, appSecret: String): String = {
    val algorithm = "HmacSHA256"
    val mac = Mac.getInstance(algorithm)
    val secretKey = new SecretKeySpec(appSecret.getBytes(StandardCharsets.UTF_8), algorithm)
    mac.init(secretKey)
    val hashBytes = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8))
    val hashHex = hashBytes.map("%02x".format(_)).mkString
    s"sha256=$hashHex"
  }

  def main(args: Array[String]): Unit = {
    val appSecret = "e573144e256f7bb561a390970a713ad0"
    val payload = """{
  "object": "page",
  "entry": [
    {
      "id": "614100828455301",
      "time": 1741604021,
      "changes": [
        {
          "field": "leadgen",
          "value": {
            "leadgen_id": "622997330688708",
            "page_id": "614100828455301",
            "form_id": "984937806518064",
            "created_time": 1741604019
          }
        }
      ]
    }
  ]
}"""
    val signature = generateSha256Signature(payload, appSecret)
    println(s"X-Hub-Signature: $signature")
  }
}