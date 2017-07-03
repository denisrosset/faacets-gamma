package com.faacets.data.instances

import cats.data.Validated

import scala.collection.immutable.ListMap
import com.faacets.consolidate._
import com.faacets.consolidate.syntax.merge._
import com.faacets.consolidate.instances.all._
import io.circe._
import spire.algebra.Semigroup

trait ListMapInstances {

  implicit def listMapStringDecoder[V:Decoder] = new Decoder[ListMap[String, V]] {

    def apply(a: HCursor) = decodeAccumulating(a).leftMap(_.head).toEither

    implicit object listMapSemigroup extends Semigroup[ListMap[String, V]] {
      def combine(x: ListMap[String, V], y: ListMap[String, V]): ListMap[String, V] = x ++ y
    }

    override def decodeAccumulating(a: HCursor): AccumulatingDecoder.Result[ListMap[String, V]] =
      a.fields match {
        case None => Validated.invalidNel(DecodingFailure("[V]ListMap[String, V]", a.history))
        case Some(s) =>
          def spin(x: List[String], m: AccumulatingDecoder.Result[ListMap[String, V]]): AccumulatingDecoder.Result[ListMap[String, V]] =
            x match {
             case Nil => m
             case hd :: tl =>
               val res: AccumulatingDecoder.Result[ListMap[String, V]] =
                 Validated.fromEither(a.get[V](hd).map(v => ListMap(hd -> v))).toValidatedNel
               spin(tl, m.combine(res))
           }
        spin(s.toList, Validated.Valid(ListMap.empty[String, V]))
    }

  }

  implicit def listMapStringEncodeJson[V:Encoder]: Encoder[ListMap[String, V]] =
    Encoder[ListMap[String, V]](lm => Json.obj(lm.toSeq.map { case (k, v) => (k, Encoder[V].apply(v)) }: _*))

}