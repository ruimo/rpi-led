scalaVersion := "2.13.1"

name := "rpi-led"
organization := "com.ruimo"

lazy val root = (project in file(".")).
  enablePlugins(UniversalDeployPlugin, JavaAppPackaging, BuildInfoPlugin).
  settings(
    buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion),
    buildInfoPackage := "generated"
  )

libraryDependencies += "org.eclipse.jetty" % "jetty-server" % "9.4.31.v20200723"
libraryDependencies += "javax" % "javaee-api" % "8.0"
libraryDependencies += "com.github.kxbmap" %% "configs" % "0.6.0"
libraryDependencies += "org.specs2" %% "specs2-core" % "4.10.0" % "test"

publishTo := Some(
  Resolver.file(
    "rpi-led",
    new File(Option(System.getenv("RELEASE_DIR")).getOrElse("/tmp"))
  )
)
