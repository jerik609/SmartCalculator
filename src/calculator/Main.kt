package calculator

import calculator.core.TaskEvaluator
import calculator.core.VariablePool
import calculator.input.Input
import java.util.*

const val debugActivated = false

fun debugMePrintln(msg: String) {
    if (debugActivated) println(msg)
}

fun debugMePrint(msg: String) {
    if (debugActivated) print(msg)
}

fun eval(x: Boolean) {
    debugMePrintln(if (x) "ok" else "NOT OK")
}

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
    debugMePrint("Must be able to process simple input with constant priority: ")
        .also { check((taskEvaluator.processInput("2 - 3 + 2 - 5".split(" ")) == -4.0).also { eval(it) }) }
    debugMePrint("Must be able to process input with increase in priority: ")
        .also { check((taskEvaluator.processInput("5 - 3 * 2 - 6".split(" ")) == -7.0).also { eval(it) }) }
    debugMePrint("Must be able to process input with increase in priority, but not decreasing to base level: ")
        .also { check((taskEvaluator.processInput("2 + 3 ^ 2 * 2".split(" ")) == 20.0).also { eval(it) }) }
    debugMePrint("Process valid variable assignment: ")
        .also { check((Input.isValidVariableAssignment("    adsakdjs =  34398  ")).also { eval(it) }) }
    debugMePrint("Process invalid variable assignment: ")
        .also { check((!Input.isValidVariableAssignment("    ads3akdjs =  34398  ")).also { eval(it) }) }
    debugMePrint("Process valid variable inquiry: ")
        .also { check((Input.isVariableInquiry("    adsakdjs   ")).also { eval(it) }) }
    debugMePrint("Process invalid variable inquiry: ")
        .also { check((!Input.isVariableInquiry("    ads3akdjs   ")).also { eval(it) }) }
    debugMePrint("Variable assignment with invalid righthand side: ")
        .also { check((Input.isVariableAssignment("    adsakdjs =  34398  = 2 ")).also { eval(it) }) }
    debugMePrint("Term with `=`, but not a variable assignment: ")
        .also { check((!Input.isVariableAssignment("    ads3akdjs   3438 = 333  ")).also { eval(it) }) }
}
