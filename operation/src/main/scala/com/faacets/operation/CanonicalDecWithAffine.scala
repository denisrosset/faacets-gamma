package com.faacets.operation

import cats.kernel.Comparison
import com.faacets.core.{LexicographicOrder, Relabeling, Scenario}
import spire.algebra.partial.PartialAction
import io.circe.{Encoder, Json}
import io.circe.syntax._
import spire.syntax.partialAction._
import syntax.extractor._
import com.faacets.data.instances.textable._

case class CanonicalDecWithAffine[V](affine: Affine,
                                     lifting: Lifting,
                                     reordering: Reordering,
                                     relabeling: Relabeling,
                                     canonical: V) {
  def original(implicit A: PartialAction[V, Affine], L: PartialAction[V, Lifting], O: PartialAction[V, Reordering], R: PartialAction[V, Relabeling]): V = {
    val step1 = (canonical <|+|? relabeling).get
    val step2 = (step1 <|+|? reordering).get
    val step3 = (step2 <|+|? lifting).get
    val step4 = (step3 <|+|? affine).get
    step4
  }
  def originalScenario: Scenario = lifting.target.scenario
  def canonicalScenario: Scenario = reordering.source
  def withoutAffine: (Affine, CanonicalDec[V]) = (affine, CanonicalDec(lifting, reordering, relabeling, canonical))
}

object CanonicalDecWithAffine {

  import CanonicalDec.someIfNotId

  implicit def encoder[V:Encoder]: Encoder[CanonicalDecWithAffine[V]] = new Encoder[CanonicalDecWithAffine[V]] {
    def apply(ld: CanonicalDecWithAffine[V]): Json = {
      val fields = Seq(
        someIfNotId(ld.affine).map(a => "affine" -> a.asJson),
        someIfNotId(ld.lifting).map(l => "lifting" -> l.asJson),
        someIfNotId(ld.reordering).map(o => "reordering" -> o.asJson),
        someIfNotId(ld.relabeling).map(r => "relabeling" -> r.asJson)
      ).flatten :+ ("canonical" -> ld.canonical.asJson)
      Json.obj(fields: _*)
    }
  }

}

trait CanonicalWithAffineExtractor[V] {

  def apply(v: V): CanonicalDecWithAffine[V]

}

object CanonicalWithAffineExtractor {

  def apply[V](implicit ev: CanonicalWithAffineExtractor[V]): CanonicalWithAffineExtractor[V] = ev

  implicit def forV[V: LexicographicOrder](implicit
                                           A: PartialAction[V, Affine], AE: OperationExtractor[V, Affine],
                                           L: PartialAction[V, Lifting], LE: OperationExtractor[V, Lifting],
                                           O: PartialAction[V, Reordering], OE: OperationExtractor[V, Reordering],
                                           R: PartialAction[V, Relabeling], RE: OperationExtractor[V, Relabeling]
                                          ): CanonicalWithAffineExtractor[V] =
    new CanonicalWithAffineExtractor[V] {
      def apply(original: V) = {
        val (res1, l) = original.forceExtract[Lifting].extractedPair
        val (res2, o) = res1.forceExtract[Reordering].extractedPair
        val (res3, a) = res2.forceExtract[Affine].extractedPair
        val (vPlus, rPlus) = res3.forceExtract[Relabeling].extractedPair
        val res3neg = (res3 <|+|? Affine(-1, 0)).get
        val (vMinus, rMinus) = res3neg.forceExtract[Relabeling].extractedPair
        LexicographicOrder[V].partialComparison(vPlus, vMinus) match {
          case Some(Comparison.EqualTo) | Some(Comparison.LessThan) => CanonicalDecWithAffine(a, l, o, rPlus, vPlus)
          case Some(Comparison.GreaterThan) => CanonicalDecWithAffine(a.copy(multiplier = -a.multiplier), l, o, rMinus, vMinus)
          case _ => sys.error("Cannot happen, both are in the same scenario")
        }
      }
    }

}