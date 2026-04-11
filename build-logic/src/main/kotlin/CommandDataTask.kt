import com.google.gson.GsonBuilder
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.yaml.snakeyaml.Yaml
import java.util.*

abstract class CommandDataTask : DefaultTask() {
    @OutputFile
    val destination = project.objects.fileProperty()
    @OutputFile
    val permissionDestination = project.objects.fileProperty()

    // i promise i will be safe
    @Suppress("UNCHECKED_CAST")
    @TaskAction
    fun harvest() {
        val pluginYml = project.file("src/main/resources/plugin.yml")
        if (!pluginYml.exists()) {
            logger.warn("No plugin.yml found to harvest")
            return
        }

        val messagesProps = project.rootProject.file("Essentials/src/main/resources/messages.properties")
        if (!messagesProps.exists()) {
            logger.warn("No messages.properties found to harvest")
            return
        }

        val yaml = Yaml()
        val data: Map<String, Any> = yaml.load(pluginYml.inputStream())
        val commands = data["commands"] as? Map<String, Map<String, Any>> ?: emptyMap()

        if (commands.isEmpty()) {
            logger.warn("No commands found in plugin.yml for ${project.name}")
            return
        }

        val extractedCommands = mutableMapOf<String, Map<String, Any>>()
        for ((cmd, details) in commands) {
            val detailsMap = details as? Map<String, Any> ?: emptyMap()
            val aliasesList = detailsMap["aliases"]
            val aliases = when (aliasesList) {
                is String -> listOf(aliasesList)
                is List<*> -> aliasesList.filterIsInstance<String>()
                else -> emptyList()
            }
            extractedCommands[cmd] = mapOf(
                "aliases" to aliases,
                "description" to (detailsMap["description"] ?: ""),
                "usage" to (detailsMap["usage"] ?: ""),
                "usages" to mutableListOf<Map<String, Any>>()
            )
        }

        val permissions = data["permissions"] as? Map<String, Map<String, Any>> ?: emptyMap()

        val extractedPermissions = mutableMapOf<String, Map<String, Any>>()
        for ((perm, value) in permissions) {
            val valueMap = value as? Map<String, Any> ?: emptyMap()
            extractedPermissions[perm] = mapOf(
                "default" to (valueMap["default"] ?: "op"),
                "description" to (valueMap["description"] ?: ""),
                "children" to (valueMap["children"] ?: emptyMap<String, Any>())
            )
        }

        if (extractedCommands.isEmpty()) {
            logger.warn("No commands found in plugin.yml for ${project.name}")
        } else {

            val properties = Properties()
            messagesProps.inputStream().use { properties.load(it) }

            properties.forEach { key, value ->
                val commandKeyRegex = Regex("^(\\w+)Command(Description|Usage)(\\d*)$")
                val match = commandKeyRegex.matchEntire(key.toString())

                if (match != null) {
                    val (command, type, index) = match.destructured
                    val commandData = extractedCommands[command] ?: return@forEach

                    if (index.isEmpty()) {
                        // main description and usage
                        when (type) {
                            "Description" -> extractedCommands[command] =
                                commandData + ("description" to value.toString())

                            "Usage" -> extractedCommands[command] = commandData + ("usage" to value.toString())
                        }
                    } else {
                        val usagesList = commandData["usages"] as MutableList<Map<String, Any>>
                        usagesList.add(
                            mapOf(
                                "usage" to value.toString(),
                                "description" to (properties["${command}CommandUsage${index}Description"]?.toString() ?: "")
                            )
                        )
                    }
                }
            }

            val json = GsonBuilder().create().toJson(extractedCommands)
            val output = project.file("build/generated/${project.name}-commands.json")
            output.parentFile.mkdirs()
            output.writeText(json)
            destination.get().asFile.parentFile.mkdirs()
            output.copyTo(destination.get().asFile, overwrite = true)
        }

        if (extractedPermissions.isEmpty()) {
            logger.warn("No permissions found in plugin.yml for ${project.name}")
        } else {
            val json = GsonBuilder().create().toJson(extractedPermissions)
            val output = project.file("build/generated/${project.name}-permissions.json")
            output.parentFile.mkdirs()
            output.writeText(json)
            permissionDestination.get().asFile.parentFile.mkdirs()
            output.copyTo(permissionDestination.get().asFile, overwrite = true)
        }
    }
}
