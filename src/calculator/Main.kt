package calculator

import calculator.core.TaskEvaluator
import java.util.*

const val debugActivated = true

fun debugMe(msg: String) {
    if (debugActivated) println(msg)
}

fun main() {

    tests()

    val scanner = Scanner(System.`in`)
    scanner.useLocale(Locale.US)

    val controller = Controller(scanner, TaskEvaluator())

    controller.mainLoop()

    // 2 + 3 ^ 2 * 2
}

fun tests() {
    val taskEvaluator = TaskEvaluator()
    check(taskEvaluator.processInput("2 - 3 + 2 - 5".split(" ")) == -4.0).also { println("=====1=====") }
    check(taskEvaluator.processInput("5 - 3 * 2 - 6".split(" ")) == -7.0).also { println("=====2=====") }
    check(taskEvaluator.processInput("2 + 3 ^ 2 * 2".split(" ")) == 20.0).also { println("=====3=====") }
}