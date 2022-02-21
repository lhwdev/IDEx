// utils

enum class Os(val id: String) {
	Linux("linux"),
	Windows("windows"),
	MacOS("macos")
}

val currentOs: Os by lazy {
	val os = System.getProperty("os.name")
	when {
		os.equals("Mac OS X", ignoreCase = true) -> Os.MacOS
		os.startsWith("Win", ignoreCase = true) -> Os.Windows
		os.startsWith("Linux", ignoreCase = true) -> Os.Linux
		else -> error("Unknown OS name: $os")
	}
}

fun includeSubprojectsOf(root: String? = null, of: String) {
	File(rootDir, if(root == null) of else "${root.replace(':', '/')}/$of").list { dir, name ->
		File(dir, "$name/build.gradle.kts").exists()
	}!!.forEach { include(if(root == null) ":$of:$it" else ":$root:$of:$it") }
}


// projects

includeSubprojectsOf(of = "ide")
include(":ide:platform:${currentOs.id}")
include(":ide:main:${currentOs.id}")
includeSubprojectsOf(of = "example")

includeBuild("includeBuild")
includeBuild("tools")


// flags

enableFeaturePreview("GRADLE_METADATA")


// plugins

pluginManagement {
	repositories {
		gradlePluginPortal()
		maven("https://maven.pkg.jetbrains.space/public/p/compose/dev") // Jetpack compose(org.jetbrains.compose)
	}
}

