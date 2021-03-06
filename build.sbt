// Scala and library versions go here

val scala212Version = "2.12.3"

val alascVersion = "0.14.1.3"
val catsVersion = "0.9.0"
val circeVersion = "0.8.0"
val circeYamlVersion = "0.6.1"
val consolidateVersion = "0.6"
val fastParseVersion = "0.4.2"
val gluonVersion = "0.1"
val scalinVersion = "0.14.1.0"
val shapelessVersion = "2.3.2"
val spireVersion = "0.14.1"
val spireCycloVersion = "0.14.1.2"

// dependency for tests only

val disciplineVersion = "0.7.2"
val scalaCheckVersion = "1.13.4"
val scalaTestVersion = "3.0.1"

lazy val faacets = (project in file("."))
  .settings(moduleName := "faacets")
  .settings(faacetsSettings)
  .settings(noPublishSettings)
  .aggregate(core, data, operation, laws, tests)
  .dependsOn(core, data, operation, laws, tests)

lazy val data = (project in file("data"))
  .settings(moduleName := "faacets-data")
  .settings(faacetsSettings: _*)
  .settings(commonJvmSettings: _*)

lazy val laws = (project in file("laws"))
  .settings(moduleName := "faacets-laws")
  .settings(faacetsSettings: _*)
  .settings(lawsSettings:_*)
  .settings(commonJvmSettings: _*)
  .dependsOn(core, data, operation)

lazy val core = (project in file("core"))
  .settings(moduleName := "faacets-core")
  .settings(faacetsSettings)
  .settings(commonJvmSettings: _*)
  .dependsOn(data)

lazy val operation = (project in file("operation"))
  .settings(moduleName := "faacets-operation")
  .settings(faacetsSettings)
  .settings(commonJvmSettings: _*)
  .dependsOn(core, data)

lazy val comp = (project in file("comp"))
  .settings(moduleName := "faacets-comp")
  .settings(faacetsSettings)
  .settings(commonJvmSettings: _*)
  .dependsOn(core, data, operation)

lazy val docs = (project in file("docs"))
  .enablePlugins(MicrositesPlugin)
  .settings(moduleName := "faacets-docs")
  .settings(faacetsSettings)
  .settings(docsSettings)
  .dependsOn(core, data, operation)

lazy val tests = (project in file("tests"))
  .settings(moduleName := "faacets-tests")
  .settings(faacetsSettings: _*)
  .settings(testSettings:_*)
  .settings(noPublishSettings:_*)
  .settings(commonJvmSettings: _*)
  .dependsOn(core, data, laws, operation)

lazy val docsSettings = Seq(
  micrositeName := "Faacets",
  micrositeDescription := "A library for Bell expressions",
  micrositeAuthor := "Jean-Daniel Bancal, Denis Rosset",
  micrositeBaseUrl := "/faacets-gamma", // baseurl is present in the `INSTALL.md` instructions
  micrositeDocumentationUrl := "/faacets-gamma/",
  micrositeGithubOwner := "denisrosset",
  micrositeGithubRepo := "faacets-gamma",
  micrositeCDNDirectives := microsites.CdnDirectives(
    jsList = List(
      "https://cdn.mathjax.org/mathjax/latest/MathJax.js?config=TeX-AMS-MML_HTMLorMML"
    )
  ),
  micrositePalette := Map(
    "brand-primary"     -> "#E05236",
    "brand-secondary"   -> "#3F3242",
    "brand-tertiary"    -> "#2D232F",
    "gray-dark"         -> "#453E46",
    "gray"              -> "#837F84",
    "gray-light"        -> "#E3E2E3",
    "gray-lighter"      -> "#F4F3F4",
    "white-color"       -> "#FFFFFF"),
  micrositeConfigYaml := microsites.ConfigYml(
    yamlCustomProperties = Map("baseUrl" -> "/faacets")
  ),
  fork in tut := true
)


lazy val faacetsSettings = buildSettings ++ commonSettings ++ publishSettings

lazy val buildSettings = Seq(
  organization := "com.faacets",
  scalaVersion := scala212Version
//  crossScalaVersions := Seq(scala211Version, scala210Version) no cross-build yet
)

lazy val commonSettings = Seq(
  scalacOptions ++= commonScalacOptions.diff(Seq(
    "-Xfatal-warnings", "-Ywarn-numeric-widen"
  )),
  resolvers ++= Seq(
    "bintray/denisrosset/maven" at "https://dl.bintray.com/denisrosset/maven",
    Resolver.sonatypeRepo("snapshots"),
    Resolver.sonatypeRepo("releases")
  ),
  libraryDependencies ++= Seq( // update DEPENDENCIES.md on change
    "net.alasc" %% "alasc-core" % alascVersion,
    "org.typelevel" %% "cats" % catsVersion,
    "io.circe" %% "circe-core" % circeVersion,
    "io.circe" %% "circe-yaml" % circeYamlVersion,
    "com.faacets" %% "consolidate" % consolidateVersion,
    "com.lihaoyi" %% "fastparse" % fastParseVersion,
    "com.faacets" %% "gluon" % gluonVersion,
    "net.alasc" %% "scalin-core" % scalinVersion,
    "com.chuusai" %% "shapeless" % shapelessVersion,
    "org.typelevel" %% "spire" % spireVersion,
    "net.alasc" %% "spire-cyclo" % spireCycloVersion
  )
) ++ warnUnusedImport

lazy val publishSettings = Seq(
  homepage := Some(url("https://github.com/denisrosset/faacets-gamma")),
  licenses += ("MIT", url("http://opensource.org/licenses/MIT")),
  pomExtra := (
    <scm>
      <url>git@github.com:denisrosset/faacets-gamma.git</url>
      <connection>scm:git:git@github.com:denisrosset/faacets-gamma.git</connection>
    </scm>
    <developers>
      <developer>
        <id>denisrosset</id>
        <name>Denis Rosset</name>
        <url>http://github.com/denisrosset/</url>
      </developer>
      <developer>
        <id>jdbancal</id>
        <name>Jean-Daniel Bancal</name>
        <url>http://github.com/jdbancal/</url>
      </developer>
    </developers>
  ),
  bintrayRepository := "maven",
  publishArtifact in Test := false
)

lazy val noPublishSettings = Seq(
  publish := (),
  publishLocal := (),
  publishArtifact := false
)

lazy val commonScalacOptions = Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-feature",
  "-language:existentials",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-language:experimental.macros",
  "-unchecked",
  "-Xfatal-warnings",
  "-Xlint",
  "-Yno-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Ywarn-value-discard",
  "-Xfuture"
)

lazy val commonJvmSettings = Seq(
  testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-oDF")
) ++ selectiveOptimize

  // -optimize has no effect in scala-js other than slowing down the build
// do not optimize on Scala 2.10 because of optimizer bugs (cargo-cult setting
// from my experience with metal)
lazy val selectiveOptimize = 
  scalacOptions ++= {
    CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, 10)) => Seq()
      case Some((2, 11)) => Seq("-optimize")
      case Some((2, 12)) => Seq()
      case _ => sys.error("Unknown Scala version")
    }
  }

lazy val warnUnusedImport = Seq(
  scalacOptions ++= {
    CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, 10)) =>
        Seq()
      case Some((2, n)) if n >= 11 =>
        Seq("-Ywarn-unused-import")
    }
  },
  scalacOptions in (Compile, console) ~= {_.filterNot("-Ywarn-unused-import" == _)},
  scalacOptions in (Test, console) := (scalacOptions in (Compile, console)).value
)

lazy val testSettings = Seq(
  libraryDependencies ++= Seq(
    "org.scalatest" %% "scalatest" % scalaTestVersion,
    "org.typelevel" %% "discipline" % disciplineVersion,
    "org.scalacheck" %% "scalacheck" % scalaCheckVersion,
    "net.alasc" %% "alasc-laws" % alascVersion,
    "org.typelevel" %% "spire-laws" % spireVersion
  )
)

lazy val lawsSettings = Seq(
  libraryDependencies ++= Seq(
    "org.typelevel" %% "discipline" % disciplineVersion,
    "org.scalacheck" %% "scalacheck" % scalaCheckVersion,
    "net.alasc" %% "alasc-laws" % alascVersion,
    "org.typelevel" %% "spire-laws" % spireVersion
  )
)
