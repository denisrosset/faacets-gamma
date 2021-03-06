package com.faacets.data
package instances

import scala.util.Try

import spire.math.SafeLong

import io.circe._

import com.faacets.consolidate.Merge
import com.faacets.gluon.{AdapterException, ArgAdapter, ResAdapter}

trait SafeLongInstances {
  import scala.util.{Left, Right}

  import Decoder.Result

  implicit val safeLongEncoder: Encoder[SafeLong] = new Encoder[SafeLong] {
    def apply(s: SafeLong) =
      if (s.isValidInt) Json.fromInt(s.toInt) else Json.fromString(s.toString)
  }

  implicit val safeLongDecoder: Decoder[SafeLong] = new Decoder[SafeLong] {
    def apply(c: HCursor): Result[SafeLong] = {
      def failure(err: String): Either[DecodingFailure, SafeLong] = Left(DecodingFailure(err, c.history))
      def decodeNumber(jsn: JsonNumber): Either[DecodingFailure, SafeLong] = jsn.toBigInt.fold(failure(s"Number $jsn does not represent an exact integer"))(bi => Right(SafeLong(bi)))
      def decodeString(str: String): Either[DecodingFailure, SafeLong] =
        Try(BigInt(str))
          .toOption
          .fold(failure(s"String $str does not represent an integer"))(x => Right(SafeLong(x)))

      c.value.fold(
        failure("SafeLong should not be null"), // null
        x => failure("SafeLong should not be bool"), // bool
        decodeNumber, // number
        decodeString, // string
        x => failure("SafeLong should not be array"), // array
        x => failure("SafeLong should not be object") // object
      )
    }
  }

  implicit val safeLongMerge: Merge[SafeLong] = Merge.fromEquals[SafeLong]

  implicit val safeLongTextable: Textable[SafeLong] = Textable.fromParser[SafeLong](Parsers.safeLong, _.toString)

  implicit val safeLongArgAdapter: ArgAdapter[SafeLong] = ArgAdapter[BigInt].map("SafeLong")(SafeLong(_))

  implicit val safeLongResAdapter: ResAdapter[SafeLong, Double] = ResAdapter[SafeLong, Double]("SafeLong") { sl =>
    if (sl =!= SafeLong(BigDecimal(sl.toBigInt.toDouble).toBigInt))
      throw new AdapterException(s"Integer $sl does not fit in Double")
    sl.toDouble
  }

}
