package calculator

import org.scalajs.dom

import com.raquo.airstream.state.*
import com.raquo.airstream.core.*
import com.raquo.airstream.eventbus.EventBus
import scala.compiletime.ops.boolean

case class CurrentStackValue(val content: Double, val isEditing: Boolean, val justPushed: Boolean = false, val comma: Int = 0)

object Stack {
    val decimalSeparator: String = ","

    private val stack: Var[List[Double]] = Var(List())
    private val current: Var[CurrentStackValue] = Var(CurrentStackValue(0, true))

    val display: Signal[List[String]] = stack.signal.combineWithFn(current.signal) {
        (stackContent, currentContent) =>
            val firstElement: String = 
                currentContent.content.toString +
                (if currentContent.comma > 0 && math.floor(currentContent.content) == currentContent.content then decimalSeparator else "")
            firstElement +: stackContent.map(_.toString)
    }

    def negate(): Unit =
        current update { case CurrentStackValue(x, isEditing, _, comma) => CurrentStackValue(-x, isEditing, comma = comma) }

    def digit(x: Int): Unit =
        current update { case CurrentStackValue(value, isEditing, justPushed, comma) =>
            val (newValue, newComma): (Double, Int) =
                if isEditing then
                    if justPushed then
                        (x.toDouble, 0)
                    else if comma > 0 then
                        (value + (if value < 0 then -1 else 1) * x * math.pow(10, -comma), comma + 1)
                    else
                        (value * 10 + (if value < 0 then -1 else 1) * x, 0)
                else
                    stack update { value +: _ }
                    (x.toDouble, 0)

            CurrentStackValue(newValue, true, comma = newComma)
        }

    def comma(): Unit =
        current update { case CurrentStackValue(value, isEditing, justPushed, comma) =>
            val (newValue, newComma): (Double, Int) =
                if isEditing then
                    if justPushed then
                        (0.0, 1)
                    else
                        (value, if comma > 1 then comma else 1 - comma)
                else
                    stack update { value +: _ }
                    (0.0, 1)

            CurrentStackValue(newValue, true, comma = newComma)
        }

    def backspace(): Unit = 
        current update { case CurrentStackValue(value, isEditing, _, comma) =>
            val (newValue, newComma): (Double, Int) = 
                if isEditing then
                    if comma <= 1 then
                        ((if value < 0 then -1 else 1) * math.floor(math.abs(value) / 10), 0)
                    else
                        val powerOfTen = math.pow(10, comma - 2)
                        ((if value < 0 then -1 else 1) * math.floor(math.abs(value) * powerOfTen) / powerOfTen, comma - 1)
                else
                    (0.0, 0)

            CurrentStackValue(newValue, true, comma = newComma)
        }

    def push(): Unit = 
        current update { case CurrentStackValue(value, _, _, _) => 
            stack update { value +: _ }
            CurrentStackValue(value, true, justPushed = true)
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
                    case CurrentStackValue(y, _, _, _) => CurrentStackValue(f(x, y), false)
                }
                rest
            case Nil => Nil
        }

    def add(): Unit = binaryOperator(_ + _)
    def sub(): Unit = binaryOperator(_ - _)
    def mult(): Unit = binaryOperator(_ * _)
    def div(): Unit = binaryOperator(_ / _)
}
