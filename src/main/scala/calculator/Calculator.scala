package calculator

import scala.scalajs.js
import org.scalajs.dom
import com.raquo.laminar.api.L.{*, given}

@main
def Calculator(): Unit =
  renderOnDomContentLoaded(
    dom.document.getElementById("app"),
    Main.appElement()
  )

object Main:
  def appElement(): Element =
    div(
      h1("Hello World Laminar!"),
    )
