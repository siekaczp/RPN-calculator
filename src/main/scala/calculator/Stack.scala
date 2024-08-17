package calculator

import org.scalajs.dom

import com.raquo.airstream.state.*
import com.raquo.airstream.core.*
import com.raquo.airstream.eventbus.EventBus
import scala.compiletime.ops.boolean

case class CurrentStackValue(val content: Double, val isEditing: Boolean, val justPushed: Boolean = false)

object Stack {
    private val stack: Var[List[Double]] = Var(List())
    private val current: Var[CurrentStackValue] = Var(CurrentStackValue(0, true))

    val display: Signal[List[String]] = stack.signal.combineWithFn(current.signal) {
        (stackContent, currentContent) =>
            currentContent.content.toString +: stackContent.map(_.toString)
    }

    def negate(): Unit =
        current update { case CurrentStackValue(x, isEditing, _) => CurrentStackValue(-x, isEditing) }

    def digit(x: Int): Unit =
        current update { case CurrentStackValue(value, isEditing, justPushed) =>
            if isEditing then
                if justPushed then
                    CurrentStackValue(x, true)
                else
                    CurrentStackValue(value * 10 + x, true)
            else
                stack update { value +: _ }
                CurrentStackValue(x.toDouble, true)
        }

    def backspace(): Unit = 
        current update { case CurrentStackValue(value, isEditing, _) =>
            if isEditing then
                CurrentStackValue(math.floor(value / 10), true)
            else
                CurrentStackValue(0.0, true)
        }

    def push(): Unit = 
        current update { case CurrentStackValue(value, isEditing, _) => 
            stack update { value +: _ }
            CurrentStackValue(value, true, true)
        }

    def pop(): Unit =
        stack update { content => 
            current set CurrentStackValue(content.headOption.getOrElse(0.0), content.isEmpty)
            content.drop(1)
        }

    private def binaryOperator(f: (Double, Double) => Double): Unit =
        stack update { _ match
            case x :: rest => 
                current update {
                    case CurrentStackValue(y, _, _) => CurrentStackValue(f(x, y), false)
                }
                rest
            case Nil => Nil
        }

    def add(): Unit = binaryOperator(_ + _)
    def sub(): Unit = binaryOperator(_ - _)
    def mult(): Unit = binaryOperator(_ * _)
    def div(): Unit = binaryOperator(_ / _)
}
