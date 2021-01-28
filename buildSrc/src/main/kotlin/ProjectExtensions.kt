import org.gradle.api.Project
import java.io.FileInputStream
import java.util.*

fun Project.getKeyProperty(propName: String): String {
    if (isRunInCI()) {
        return System.getenv(propName)
    }

    val file = rootProject.file("key.properties")
    if (file.exists()) {
        val properties = Properties()
        properties.load(FileInputStream(file))
        if (properties.containsKey(propName)) {
            return properties[propName] as String
        }
    }
    return ""
}