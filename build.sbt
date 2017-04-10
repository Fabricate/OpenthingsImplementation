name := "Open Hardware Project Documentation System"

version := "0.0.2"

organization := "at.fabricate"

scalaVersion := "2.11.8"

resolvers ++= Seq("snapshots"     at "https://oss.sonatype.org/content/repositories/snapshots",
                  "staging"       at "https://oss.sonatype.org/content/repositories/staging",
                  "releases"      at "https://oss.sonatype.org/content/repositories/releases"
                 )

seq(webSettings :_*)

unmanagedResourceDirectories in Test <+= (baseDirectory) { _ / "src/main/webapp" }

classpathTypes ~= (_ + "openthings")

EclipseKeys.withSource := true

scalacOptions ++= Seq("-deprecation", "-unchecked")

libraryDependencies ++= {
  val liftVersion = "3.0.1"
  Seq(
    "net.liftweb"       %% "lift-webkit"        % liftVersion        % "compile",
    "net.liftweb"       %% "lift-mapper"        % liftVersion        % "compile",
    "org.eclipse.jetty" % "jetty-webapp"        % "8.1.17.v20150415"  % "container,test",
    "org.eclipse.jetty" % "jetty-plus"          % "8.1.17.v20150415"  % "container,test", // For Jetty Config
    "org.eclipse.jetty.orbit" % "javax.servlet" % "3.0.0.v201112011016" % "container,test" artifacts Artifact("javax.servlet", "jar", "jar"),
    "ch.qos.logback" 	% "logback-classic" 	% "1.1.3",
    "org.specs2"        %% "specs2-core"        % "3.6.4"           % "test",
    "com.h2database" 	% "h2" % "1.4.187",
    "net.liftmodules"   %% "widgets_3.0"        % "1.4-SNAPSHOT",
    "net.liftmodules"   %% "imaging_3.0"        % "1.4-SNAPSHOT",
    "net.liftmodules"   %% "lift-jquery-module_3.0" % "2.9",
    "org.eclipse.jgit"  % "org.eclipse.jgit"    % "3.7.1.201504261725-r",
    "mysql"             %"mysql-connector-java" % "5.1.41",
    "com.github.tkqubo" % "html-to-markdown" 	% "0.3.0"
  )
}



scalacOptions in Test ++= Seq("-Yrangepos")
