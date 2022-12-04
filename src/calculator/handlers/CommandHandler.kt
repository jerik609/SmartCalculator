package calculator.handlers

import calculator.debugMePrintln
import calculator.input.Command
import calculator.input.Input
import calculator.input.UnknownCommandException
import java.util.concurrent.atomic.AtomicBoolean

class CommandHandler(private val terminate: AtomicBoolean): Handler {

    private fun printHelp() {
        println("The program calculates the sum of numbers")
    }

    private fun performCommand(command: Command) {
        when(command) {
            Command.EXIT -> {
                println("Bye!")
                terminate.compareAndSet(false, true)
            }
            Command.HELP -> printHelp()
            else -> println("Unhandled command: $command")
        }
    }

    override fun isForMe(input: String): Boolean {
        val input = input.split(" ").map { it.trim() }
        return input[0][0] == '/'
    }

    override fun handle(input: String) {
        debugMePrintln(">>> command handler <<<")
        try {
            val command = Input.translateToCommand(input)
            performCommand(command)
        } catch (e: UnknownCommandException) {
            println("Unknown command")
        }
    }

}
