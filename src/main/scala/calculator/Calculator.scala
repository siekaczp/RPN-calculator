package calculator

import scala.scalajs.js
import scala.scalajs.js.annotation.*

import org.scalajs.dom

@main
def Calculator(): Unit =
  dom.document.querySelector("#app").innerHTML = s"""
    <div>
      <h1>Hello World of Scala.js!</h1>
    </div>
  """
