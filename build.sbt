enablePlugins(JavaAppPackaging)

import Dependencies._

ThisBuild / scalaVersion     := "2.13.3"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.cricanr"
ThisBuild / organizationName := "playground"

val circeVersion = "0.13.0"

lazy val root = (project in file("."))
  .settings(
    name := "ratinganalysis",
    libraryDependencies ++= Seq(
      "com.github.tototoshi" %% "scala-csv" % "1.3.6",
      "org.typelevel" %% "cats-core" % "2.2.0",
      "org.typelevel" %% "cats-laws" % "2.2.0",
      "io.circe" %% "circe-core" % circeVersion,
      "io.circe" %% "circe-generic" % circeVersion,
      "io.circe" %% "circe-parser" % circeVersion,
      "com.typesafe" % "config" % "1.4.1",
      "net.codingwell" %% "scala-guice" % "4.2.11",
      "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
      "org.scalatestplus" %% "mockito-3-4" % "3.2.2.0" % "test",
      scalaTest % Test)
  )

mappings in Universal += file("src/main/resources/ratings.csv") -> "/src/main/resources/ratings.csv"
