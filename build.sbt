scalaVersion := "2.13.1"

name := "rpi-led"
organization := "com.ruimo"

libraryDependencies += "org.eclipse.jetty" % "jetty-server" % "9.4.31.v20200723"
libraryDependencies += "javax" % "javaee-api" % "8.0"

publishTo := Some(
  Resolver.file(
    "rpi-led",
    new File(Option(System.getenv("RELEASE_DIR")).getOrElse("/tmp"))
  )
)
