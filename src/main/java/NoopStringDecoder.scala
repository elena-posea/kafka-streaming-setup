import kafka.serializer.Decoder
import kafka.utils.VerifiableProperties;

class NoopStringDecoder(props: VerifiableProperties) extends Decoder[String] {
  def fromBytes(input: Array[Byte]): String = {
    input.toString
  }
}
