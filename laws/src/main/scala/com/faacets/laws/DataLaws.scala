package com.faacets.laws

import cats.data.{Validated, NonEmptyList => Nel}
import spire.algebra.Eq
import spire.syntax.eq._

import io.circe._
import io.circe.yaml.parser
import io.circe.yaml.syntax._
import org.scalacheck.Arbitrary
import org.scalacheck.Prop._
import org.typelevel.discipline.Laws

import com.faacets.data.Textable
import com.faacets.data.syntax.all._

object DataLaws {

  def apply[A : Eq : Arbitrary] = new DataLaws[A] {
    def Equ = Eq[A]
    def Arb = implicitly[Arbitrary[A]]
  }

}

trait DataLaws[A] extends Laws {

  implicit def Equ: Eq[A]
  implicit def Arb: Arbitrary[A]

  def coded(implicit d: Decoder[A], e: Encoder[A]) =
    new SimpleRuleSet(
      "coded",

      "through JSON AST" → forAll( (a: A) =>
        Decoder[A].apply(HCursor.fromJson(Encoder[A].apply(a))).fold( _ => false, _ === a)
      ),

      "through YAML" → forAll { (a: A) =>


        val yml = Encoder[A].apply(a).asYaml.spaces2
        val res = Validated.fromEither(parser.parse(yml))
          .leftMap(x => Nel.of(x))
          .andThen(_.asAcc[A])

        res === Validated.valid(a)
      }
    )

  def textable(implicit ev: Textable[A]) =
    new SimpleRuleSet(
      "textable",

      "a.toText.fromText[A] === a" → forAll( (a: A) =>
        a.toText.parseUnsafe[A] === a
      )

    )

}
