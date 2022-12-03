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
}
