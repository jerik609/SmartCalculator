package calculator

import calculator.core.Controller
import calculator.core.TaskEvaluator
import calculator.core.VariablePool
import calculator.input.Input
import java.util.*

const val debugActivated = true

fun debugMePrintln(msg: String) {
    if (debugActivated) println(msg)
}

fun debugMePrint(msg: String) {
    if (debugActivated) print(msg)
}

fun eval(x: Boolean) {
    debugMePrintln(if (x) "* TEST OK *" else "!!! TEST NOT OK !!!")
}

/**
 * notes:
 *
 * 1. when receiving this stupid input with "+(<number>" numbers slapped on each other without spaces - just use
 * regex to add spaces and then tokenize
 * 2. brackets are rather simple - write them as operators
 * when "(" bracket - increase the priority, like infinite priority, just write (including the bracket) and continue
 * when ")" bracket - start evaluating until other bracket is reached ... it should be possible, it's like
 * the whole calculation was in between two "master brackets"
 *
 *
 */

fun main() {

    tests()

    val scanner = Scanner(System.`in`)
    scanner.useLocale(Locale.US)

    val controller = Controller(scanner)

    controller.mainLoop()
}

fun tests() {
    val variablePool = VariablePool()
    val taskEvaluator = TaskEvaluator(variablePool)
    debugMePrintln("Must be able to process simple input with constant priority: ")
        .also { check((taskEvaluator.processInput("2 - 3 + 2 - 5") == -4.0).also { eval(it) }) }
    debugMePrintln("Must be able to process input with increase in priority: ")
        .also { check((taskEvaluator.processInput("5 - 3 * 2 - 6") == -7.0).also { eval(it) }) }
    debugMePrintln("Must be able to process input with increase in priority, but not decreasing to base level: ")
        .also { check((taskEvaluator.processInput("2 + 3 ^ 2 * 2") == 20.0).also { eval(it) }) }
    debugMePrintln("Process valid variable assignment: ")
        .also { check((Input.isValidVariableAssignment("    adsakdjs =  34398  ")).also { eval(it) }) }
    debugMePrintln("Process invalid variable assignment: ")
        .also { check((!Input.isValidVariableAssignment("    ads3akdjs =  34398  ")).also { eval(it) }) }
    debugMePrintln("Process valid variable inquiry: ")
        .also { check((Input.isVariableInquiry("    adsakdjs   ")).also { eval(it) }) }
    debugMePrintln("Process invalid variable inquiry: ")
        .also { check((!Input.isVariableInquiry("    ads3akdjs   ")).also { eval(it) }) }
    debugMePrintln("Variable assignment with invalid righthand side: ")
        .also { check((Input.isVariableAssignment("    adsakdjs =  34398  = 2 ")).also { eval(it) }) }
    debugMePrintln("Term with `=`, but not a variable assignment: ")
        .also { check((!Input.isVariableAssignment("    ads3akdjs   3438 = 333  ")).also { eval(it) }) }
    debugMePrintln("Evaluate task without spaces: ")
        .also { check((taskEvaluator.processInput("5*2+2*3-7*2+3^2-10") == 1.0).also { eval(it) }) }
    debugMePrintln("Evaluate task without spaces (with brackets): ")
        .also { check((taskEvaluator.processInput("5*2+2*3-7*(2+3)") == -19.0).also { eval(it) }) }
}
