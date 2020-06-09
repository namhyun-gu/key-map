import org.gradle.api.Project
import java.io.File
import java.io.FileInputStream
import java.util.*
import java.util.concurrent.TimeUnit

fun Project.getKey(propName: String): String {
    val file = rootProject.file("key.properties")
    if (file.exists()) {
        val properties = Properties()
        properties.load(FileInputStream(file))
        if (properties[propName] != null) {
            return "${properties[propName]}"
        }
    }
    return System.getenv(propName)
}

fun String.runCommand(workingDir: File = File("./")): String {
    val parts = this.split("\\s".toRegex())
    val proc = ProcessBuilder(*parts.toTypedArray())
        .directory(workingDir)
        .redirectOutput(ProcessBuilder.Redirect.PIPE)
        .redirectError(ProcessBuilder.Redirect.PIPE)
        .start()

    proc.waitFor(1, TimeUnit.MINUTES)
    return proc.inputStream.bufferedReader().readText().trim()
}