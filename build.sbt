import org.scalajs.linker.interface.ModuleSplitStyle

lazy val calculator = project.in(file("."))
  .enablePlugins(ScalaJSPlugin)
  .settings(
    scalaVersion := "3.3.3",

    scalaJSUseMainModuleInitializer := true,

    scalaJSLinkerConfig ~= {
      _.withModuleKind(ModuleKind.ESModule)
        .withModuleSplitStyle(
          ModuleSplitStyle.SmallModulesFor(List("calculator")))
    },

    libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "2.8.0",
  )
