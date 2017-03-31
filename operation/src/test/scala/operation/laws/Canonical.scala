package com.faacets
package operation
package laws
/*
import org.scalacheck.{Arbitrary, Gen}

import spire.math.Rational

import qalg.immutable.QVector

import core._
import data._

case class Canonical[A](a: A)

object Canonical {
  val positivity = Expr(Scenario.nmk(1,1,2), NPRepresentation, QVector(-1, 1))
  val i3322 = Expr(Scenario.nmk(2,3,2), NPRepresentation, QVector(-5, 3, -5, 3, -4, 2, 3, -1, 3, -1, 4, -2, -5, 3, -5, 3, 2, -4, 3, -1, 3, -1, -2, 4, -4, 4, 2, -2, 0, 0, 2, -2, -4, 4, 0, 0))
  val chsh = Expr(Scenario.nmk(2,2,2), NPRepresentation, QVector(-1, 1, -1, 1, 1, -1, 1, -1, -1, 1, 1, -1, 1, -1, -1, 1))
  val cglmp3 = Expr(Scenario.nmk(2,2,3), NPRepresentation, QVector(-1, 0, 1, -1, 0, 1, 0, 1, -1, 1, -1, 0, 1, -1, 0, 0, 1, -1, -1, 1, 0, -1, 1, 0, 0, -1, 1, 1, 0, -1, 1, 0, -1, 0, -1, 1))
  val mermin = Expr(Scenario.nmk(3,2,2), NPRepresentation, QVector(-1, 1, 0, 0, 1, -1, 0, 0, 0, 0, -1, 1, 0, 0, 1, -1, 1, -1, 0, 0, -1, 1, 0, 0, 0, 0, 1, -1, 0, 0, -1, 1, 0, 0, -1, 1, 0, 0, 1, -1, 1, -1, 0, 0, -1, 1, 0, 0, 0, 0, 1, -1, 0, 0, -1, 1, -1, 1, 0, 0, 1, -1, 0, 0))
  val pironio = Expr("[(3 2) (2 2 2)]".fromText[Scenario], NPRepresentation, QVector(-7, 5, 5, -3, 5, 7, -5, -5, 7, -9, -5, -5, 7, 7, -9, 5, 5, -7, -3, 5, -5, 7, -5, 7, -9, 5, -7, 5, -3, 5))

  def genExpr: Gen[Expr] = Gen.oneOf(positivity, chsh, cglmp3, mermin, pironio)
  def genCanonical: Gen[Canonical[Expr]] = genExpr.map(Canonical(_))
  implicit def arbCanonicalExpr: Arbitrary[Canonical[Expr]] = Arbitrary(genCanonical)
}
*/