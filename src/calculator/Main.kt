package calculator

import calculator.core.Controller
import calculator.core.TaskEvaluator
import calculator.data.VariablePool
import calculator.input.Input
import java.math.BigInteger
import java.util.*

const val debugActivated = true

fun debugMePrintln(msg: String) {
    if (debugActivated) println(msg)
}

fun debugMePrint(msg: String) {
    if (debugActivated) print(msg)
}

fun eval(result: Boolean) {
    debugMePrintln(if (result) "* TEST OK *" else "!!! TEST NOT OK !!!")
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
    debugMePrintln("Must be able to process simple input with constant priority: ")
        .also { check((taskEvaluator.processInput("2 - 3 + 2 - 5") == BigInteger.valueOf(-4)).also { eval(it) }) }
    debugMePrintln("Must be able to process input with increase in priority: ")
        .also { check((taskEvaluator.processInput("5 - 3 * 2 - 6") == BigInteger.valueOf(-7)).also { eval(it) }) }
    debugMePrintln("Must be able to process input with increase in priority, but not decreasing to base level: ")
        .also { check((taskEvaluator.processInput("2 + 3 ^ 2 * 2") == BigInteger.valueOf(20)).also { eval(it) }) }
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
        .also { check((taskEvaluator.processInput("5*2+2*3-7*2+3^2-10") == BigInteger.valueOf(1)).also { eval(it) }) }
    debugMePrintln("Evaluate task with simple brackets: ")
        .also { check((taskEvaluator.processInput("(2+3)") == BigInteger.valueOf(5)).also { eval(it) }) }
    debugMePrintln("Evaluate task without spaces (with brackets): ")
        .also { check((taskEvaluator.processInput("5*2+2*3-7*(2+3)") == BigInteger.valueOf(-19)).also { eval(it) }) }
    debugMePrintln("Evaluate task with nested brackets (with brackets): ")
        .also { check((taskEvaluator.processInput("(((((2+3) )) ))") == BigInteger.valueOf(5)).also { eval(it) }) }
    debugMePrintln("Evaluate task from examples (with brackets): ")
        .also { check((taskEvaluator.processInput("3 + 8 * ((4 + 3) * 2 + 1) - 6 / (2 + 1)") == BigInteger.valueOf(121)).also { eval(it) }) }
    debugMePrintln("Another crazy test from academy: ")
        .also { check((taskEvaluator.processInput("5 --- 2 ++++++ 4 -- 2 ---- 1") == BigInteger.valueOf(10)).also { eval(it) }) }

    /*
    I could really benefit from negative test cases:
    > 8 * (2 + 3
    Invalid expression
    > 4 + 5)
    Invalid expression
    > 2 ************ 2
    Invalid expression
    > 2 // 2
     */

}

//> n = 32000000000000000000
//> 33000000000000000000 + 20000000000000000000 + 11000000000000000000 + 49000000000000000000 - 32000000000000000000 - 9000000000000000000 + 1000000000000000000 - 80000000000000000000 + 4000000000000000000 + 1
//164888577