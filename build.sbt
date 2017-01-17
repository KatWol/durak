name          := "durak"
organization  := "de.htwg.se"
version       := "0.0.1"
scalaVersion  := "2.11.8"
scalacOptions := Seq("-unchecked", "-feature", "-deprecation", "-encoding", "utf8")

resolvers += Resolver.jcenterRepo

libraryDependencies ++= {
  val scalaTestV       = "3.0.0-M15"
  val scalaMockV       = "3.2.2"
  Seq(
    "org.scalatest" %% "scalatest"                   % scalaTestV       % "test",
    "org.scalamock" %% "scalamock-scalatest-support" % scalaMockV       % "test"
  )
}

libraryDependencies += "org.scala-lang.modules" % "scala-swing_2.11" % "1.0.1"

libraryDependencies += "junit" % "junit" % "4.8" % "test"
libraryDependencies += "com.google.inject" % "guice" % "3.0"
libraryDependencies += "com.google.inject.extensions" % "guice-assistedinject" % "3.0"
connectInput in run := true
