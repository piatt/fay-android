pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "Fay"

/**
 * Automatically and recursively search and include all modules
 * in the project that contain a build.gradle.kts file.
 */
fun includeModules(path: String = ".", prefix: String = "") {
    val dir = file(path)
    // Skip directories that should not be treated as modules
    val excludedDirs = setOf(
        ".git",
        ".gradle",
        ".idea",
        "build",
        "buildSrc",
        "build-logic",
        "gradle",
        "src",
        ".kotlin"
    )
    // Skip the root directory itself
    if (dir == rootDir && prefix.isEmpty()) {
        dir.listFiles()?.forEach { subDir ->
            if (subDir.isDirectory && !excludedDirs.contains(subDir.name)) {
                includeModules(subDir.path, subDir.name)
            }
        }
        return
    }
    // Check if current directory is a valid module
    val buildFile = File(dir, "build.gradle.kts")
    if (buildFile.exists()) {
        val modulePath = if (prefix.isEmpty()) ":" else ":$prefix"
        include(modulePath)
    }
    // Recursively check subdirectories
    dir.listFiles()?.forEach { subDir ->
        if (subDir.isDirectory && !excludedDirs.contains(subDir.name)) {
            val newPrefix = if (prefix.isEmpty()) subDir.name else "$prefix:${subDir.name}"
            includeModules(subDir.path, newPrefix)
        }
    }
}
includeModules()
