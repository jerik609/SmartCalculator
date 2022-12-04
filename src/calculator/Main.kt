package calculator

import calculator.core.TaskEvaluator
import calculator.input.Controller
import java.util.*

const val debugActivated = false

fun debugMe(msg: String) {
    if (debugActivated) println(msg)
}

fun main() {

    tests()

    val scanner = Scanner(System.`in`)
    scanner.useLocale(Locale.US)

    val controller = Controller(scanner, TaskEvaluator())

    controller.mainLoop()
}

fun tests() {
    val taskEvaluator = TaskEvaluator()
    check(taskEvaluator.processInput("2 - 3 + 2 - 5".split(" ")) == -4.0).also { debugMe("=====1=====") }
    check(taskEvaluator.processInput("5 - 3 * 2 - 6".split(" ")) == -7.0).also { debugMe("=====2=====") }
    check(taskEvaluator.processInput("2 + 3 ^ 2 * 2".split(" ")) == 20.0).also { debugMe("=====3=====") }
    check(Controller.isValidVariableAssignment("    adsakdjs =  34398  ")).also { debugMe("=====4=====") }
    check(!Controller.isValidVariableAssignment("    ads3akdjs =  34398  ")).also { debugMe("=====5=====") }
    check(Controller.isVariableInquiry("    adsakdjs   ")).also { debugMe("=====6=====") }
    check(!Controller.isVariableInquiry("    ads3akdjs   ")).also { debugMe("=====7=====") }
    check(Controller.isVariableAssignment("    adsakdjs =  34398  = 2 ")).also { debugMe("=====4=====") }
    check(!Controller.isVariableAssignment("    ads3akdjs   3438 = 333  ")).also { debugMe("=====5=====") }
}
