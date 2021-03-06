name := "bench-rest-test"

version := "3.0.0-SNAPSHOT"

scalaVersion := "2.11.11"

resolvers ++= Seq(Classpaths.sbtPluginReleases)

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-ws" % "2.5.14",
  "com.typesafe.play" %% "play-specs2" % "2.5.14" % Test,
  "org.mockito" % "mockito-core" % "1.9.5" % Test,
  "de.leanovate.play-mockws" %% "play-mockws" % "2.5.1" % Test
)

mainClass in (Compile, run) := Some("com.oldsneerjaw.AccountBalanceApp")

coverageExcludedPackages := """com\.oldsneerjaw\.AccountBalanceApp"""
