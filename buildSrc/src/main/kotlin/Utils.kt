fun buildVersionCode(): Int {
    return if (isRunInCI()) {
        System.getenv("GITHUB_RUN_NUMBER").toInt()
    } else {
        exec("git rev-list --count HEAD").toInt()
    }
}

fun buildVersionName(): String {
    return exec("git describe --tags --abbrev=0")
}

fun exec(command: String): String {
    val process = Runtime.getRuntime().exec(command)
    return process.inputStream.bufferedReader().readText().trim()
}

fun isRunInCI(): Boolean = System.getenv("CI") == "true"