import java.util.Properties
import java.io.FileInputStream

rootProject.extra.apply {
    set("compileSdkVersion", 30)
    set("minSdkVersion", 23)
    set("targetSdkVersion", 30)
    set("versionCode", getVersionCode())
    set("versionName", "git describe --tags --abbrev=0".runCommand())

    set("NCP_CLIENT_ID", getKey("NCP_CLIENT_ID"))
    set("NCP_CLIENT_SECRET", getKey("NCP_CLIENT_SECRET"))
    set("KEYSTORE_PASSWORD", getKey("KEYSTORE_PASSWORD"))
    set("KEYSTORE_KEY_ALIAS", getKey("KEYSTORE_KEY_ALIAS"))
    set("KEYSTORE_KEY_PASSWORD", getKey("KEYSTORE_KEY_PASSWORD"))
}

fun getKey(propName: String): String {
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

fun getVersionCode(): Int {
    return if (System.getenv("CI") == "true") {
        System.getenv("GITHUB_RUN_NUMBER").toInt()
    } else {
        "git rev-list --count HEAD".runCommand().toInt()
    }
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