name := "basicAuthenticator"

organization := "net.habashi"

version := "1.0.0"

scalaVersion := "2.11.7"

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play" % "2.5.6",
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.0" % Test
)
