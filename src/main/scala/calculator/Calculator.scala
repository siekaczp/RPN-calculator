package calculator

import scala.scalajs.js
import org.scalajs.dom
import com.raquo.laminar.api.L.{*, given}
import com.raquo.airstream.core.Observer

val buttons = 
    "7894561230".map(digit => CalculatorButton(digit.toString, DigitCommand(digit.toString.toInt)))
    ++ Seq(
        CalculatorButton("+", AddCommand, Some(4), Some(3)),
        CalculatorButton("-", SubCommand, Some(5), Some(3)),
        CalculatorButton("*", MultCommand, Some(4), Some(2)),
        CalculatorButton("/", DivCommand, Some(5), Some(2)),
        CalculatorButton("DROP", DropCommand, Some(4), Some(1)),
        CalculatorButton("<-", BackspaceCommand, Some(5), Some(1)),
        CalculatorButton("+/-", NegateCommand, Some(2), Some(4)),
        CalculatorButton(",", CommaCommand, Some(3), Some(4)),
        CalculatorButton("ENTER", EnterCommand, Some(4), Some(4), width = 2),
    )

@main
def Calculator(): Unit =
    renderOnDomContentLoaded(
        dom.document.getElementById("app"),
        div(
            div(
                className := "display",
                children <-- Stack.display.splitByIndex((id, initial, valueSignal) => div(
                    className := "displayEntry",
                    child.text <-- valueSignal
                ))
            ),
            div(
                ((className := "buttons") +: buttons)*
            )
        )
    )
