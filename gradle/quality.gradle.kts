import java.io.File

fun appModulePath(): String =
    (project.findProperty("qualityAppModule") as? String)?.trim().orEmpty().ifBlank { "androidApp" }

fun appProject(): String = ":${appModulePath()}"

private val detektModuleDirs =
    setOf("domain", "data", "storage", "analytics", "billing", "architecture")

fun isKotlinSource(path: String): Boolean =
    path.endsWith(".kt") || path.endsWith(".kts")

fun hookFileOrThrow(): File {
    val path =
        project.findProperty("hookFile") as? String
            ?: throw GradleException("Missing required property: -PhookFile=<path>")
    val file = rootProject.file(path)
    if (!file.exists()) {
        throw GradleException("File not found: $path")
    }
    return file
}

fun execAndCapture(command: List<String>, workingDir: File = rootProject.projectDir): Pair<Int, String> {
    val process =
        ProcessBuilder(command)
            .directory(workingDir)
            .redirectErrorStream(true)
            .start()
    val output = process.inputStream.bufferedReader().readText().trim()
    val exitCode = process.waitFor()
    return exitCode to output
}

fun filterOutputForFile(output: String, relativePath: String, fileName: String): List<String> =
    output
        .lineSequence()
        .filter { line ->
            line.contains(relativePath) ||
                line.contains(fileName) ||
                line.contains("BUILD FAILED") ||
                line.startsWith("> ") ||
                line.contains("weighted issues")
        }
        .distinct()
        .toList()

fun detektProjectForPath(relativePath: String): String? {
    val moduleDir = relativePath.substringBefore('/')
    return moduleDir.takeIf { it in detektModuleDirs }?.let { ":$it" }
}

tasks.register("qualityFormatFile") {
    group = "quality"
    description = "Format a single file using Spotless and ktlint (for Cursor hooks)"

    doLast {
        val hookFile = hookFileOrThrow()
        val relativePath = hookFile.relativeTo(rootProject.projectDir).path.replace('\\', '/')

        if (!isKotlinSource(relativePath)) {
            logger.lifecycle("Skipping format for non-Kotlin file: $relativePath")
            return@doLast
        }

        val (exitCode, output) =
            execAndCapture(
                listOf(
                    "./gradlew",
                    "ktlintFormat",
                    "spotlessApply",
                    "-q",
                    "--no-daemon",
                    "--no-configuration-cache",
                ),
            )
        if (exitCode != 0) {
            logger.warn("Format tasks reported issues for $relativePath:\n$output")
        }
    }
}

tasks.register("qualityCheckFile") {
    group = "quality"
    description = "Check a single file using ktlint, Spotless, and Detekt (for Cursor hooks)"

    doLast {
        val hookFile = hookFileOrThrow()
        val relativePath = hookFile.relativeTo(rootProject.projectDir).path.replace('\\', '/')

        if (!isKotlinSource(relativePath)) {
            logger.lifecycle("Skipping check for non-Kotlin file: $relativePath")
            return@doLast
        }

        val violations = StringBuilder()
        violations.appendLine("Code quality issues detected in $relativePath:")
        violations.appendLine()

        var hasViolations = false

        val (ktlintExit, ktlintOutput) =
            execAndCapture(
                listOf(
                    "./gradlew",
                    "ktlintCheck",
                    "-q",
                    "--no-daemon",
                    "--no-configuration-cache",
                ),
            )
        if (ktlintExit != 0) {
            val lines = filterOutputForFile(ktlintOutput, relativePath, hookFile.name)
            if (lines.isNotEmpty()) {
                hasViolations = true
                violations.appendLine("[ktlint]")
                lines.forEach { violations.appendLine("  $it") }
                violations.appendLine()
            }
        }

        val (spotlessExit, spotlessOutput) =
            execAndCapture(
                listOf(
                    "./gradlew",
                    "spotlessCheck",
                    "-q",
                    "--no-daemon",
                    "--no-configuration-cache",
                ),
            )
        if (spotlessExit != 0) {
            val lines = filterOutputForFile(spotlessOutput, relativePath, hookFile.name)
            if (lines.isNotEmpty()) {
                hasViolations = true
                violations.appendLine("[spotless]")
                lines.forEach { violations.appendLine("  $it") }
                violations.appendLine()
            }
        }

        detektProjectForPath(relativePath)?.let { detektProject ->
            val (detektExit, detektOutput) =
                execAndCapture(
                    listOf(
                        "./gradlew",
                        "$detektProject:detekt",
                        "-q",
                        "--no-daemon",
                        "--no-configuration-cache",
                    ),
                )
            if (detektExit != 0) {
                val lines = filterOutputForFile(detektOutput, relativePath, hookFile.name)
                if (lines.isNotEmpty()) {
                    hasViolations = true
                    violations.appendLine("[detekt]")
                    lines.forEach { violations.appendLine("  $it") }
                    violations.appendLine()
                }
            }
        }

        if (hasViolations) {
            violations.appendLine("Fix these before continuing.")
            println(violations.toString())
            throw GradleException("Quality check failed for $relativePath")
        }

        logger.lifecycle("No issues found in $relativePath")
    }
}

tasks.register("qualityCheckAll") {
    group = "quality"
    description = "Run all static analyzers (for Cursor stop hook)"
    dependsOn("qualityCheck")
}
