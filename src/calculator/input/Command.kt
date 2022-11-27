package calculator.input

enum class Command(private val commandStr: String) {
    EXIT("/exit"),
    HELP("/help"),
    NOT_A_COMMAND("n/a"), ;

    fun equalsStr(commandStr: String) =
        commandStr != NOT_A_COMMAND.commandStr && commandStr == this.commandStr
}
