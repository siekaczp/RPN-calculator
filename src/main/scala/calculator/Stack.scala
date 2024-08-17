package calculator

import org.scalajs.dom

import com.raquo.airstream.state.*
import com.raquo.airstream.core.*
import com.raquo.airstream.eventbus.EventBus
import scala.compiletime.ops.boolean

object Stack {
    private val decimalSeparator = ","

    private val stack: Var[List[Double]] = Var(List(0.0))
    private val justPushed: Var[Option[Double]] = Var(None)
    val editing: Var[Boolean] = Var(true)

    val display: Signal[List[String]] = stack.signal.combineWithFn(justPushed.signal, editing.signal) {
        (stackContent, pushedContent, isEditing) =>
            val first = 
                pushedContent.orElse(stackContent.headOption).getOrElse(0).toString
                + (if !isEditing then decimalSeparator else "")

            first +: stackContent.tail.map(_.toString + decimalSeparator)
    }

    def updateCurrentValue(f: Double => Double): Unit =
        justPushed set None
        editing set true
        stack update {
            content => f(content.headOption.getOrElse(0.0)) +: content.tail
        }

    def negate(): Unit =
        stack update {
            content => -content.headOption.getOrElse(0.0) +: content.tail
        }

    def push(x: Int = 0): Unit = 
        if x == 0 then justPushed set Some(stack.now().headOption.getOrElse(0))
        editing set true
        stack update { content => 
            justPushed.now().getOrElse(x.toDouble) +: content
        }

    def pop(): Unit =
        justPushed set None
        editing set false
        stack update { current => if current.length <= 1 then List(0.0) else current.tail }

    private def binaryOperator(f: (Double, Double) => Double): Unit =
        if justPushed.now().isDefined then
            stack update { content => content.tail.headOption.getOrElse(0) +: content.tail }
            justPushed set None

        editing set false
        stack update { _ match
            case y :: x :: rest => f(x, y) :: rest
            case list => list
        }

    def add(): Unit = binaryOperator(_ + _)
    def sub(): Unit = binaryOperator(_ - _)
    def mult(): Unit = binaryOperator(_ * _)
    def div(): Unit = binaryOperator(_ / _)
}
