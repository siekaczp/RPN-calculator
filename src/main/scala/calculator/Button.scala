package calculator

import org.scalajs.dom.*
import com.raquo.laminar.api.L.{*, given}
import com.raquo.laminar.nodes.ReactiveHtmlElement

val gridRowStart: StyleProp[Int] = styleProp("grid-row-start")
val gridColumnStart: StyleProp[Int] = styleProp("grid-column-start")
val gridColumnEnd: StyleProp[String] = styleProp("grid-column-end")

def CalculatorButton(label: String, command: Command, x: Option[Int] = None, y: Option[Int] = None, width: Int = 1) =
    val modifiers = modSeq(
        if width > 1 then (gridColumnEnd := "span " + width.toString) else emptyMod,
        x.map(t => gridColumnStart := t).getOrElse(emptyMod),
        y.map(t => gridRowStart := t).getOrElse(emptyMod),
    )

    button(
        className := "button",
        label,
        onClick.mapTo(command) --> CommandObserver,
        modifiers
    )
