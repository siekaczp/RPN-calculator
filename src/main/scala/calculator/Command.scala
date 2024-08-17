package calculator

import org.scalajs.dom
import com.raquo.airstream.core.Observer

sealed trait Command

case class DigitCommand(digit: Int) extends Command
case object AddCommand extends Command
case object SubCommand extends Command
case object MultCommand extends Command
case object DivCommand extends Command
case object DropCommand extends Command
case object BackspaceCommand extends Command
case object NegateCommand extends Command
case object CommaCommand extends Command
case object EnterCommand extends Command


val CommandObserver: Observer[Command] = Observer[Command] {
    case DigitCommand(x: Int) =>
        if Stack.editing.now() then
            Stack.updateCurrentValue(current => 10 * current + x)
        else
            Stack.push(x)
    case BackspaceCommand =>
        if Stack.editing.now() then
            Stack.updateCurrentValue(current => math.floor(current / 10))
        else
            Stack.updateCurrentValue(_ => 0.0)
    case DropCommand =>
        Stack.pop()
    case EnterCommand =>
        Stack.push()
    case AddCommand => 
        Stack.add()
    case SubCommand =>
        Stack.sub()
    case MultCommand =>
        Stack.mult()
    case DivCommand =>
        Stack.div()
    case NegateCommand =>
        Stack.negate()
    case CommaCommand =>
        dom.console.log("CommaCommand")
}
