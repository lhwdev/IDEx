// projects

fun includeSubprojectsOf(root: String? = null, of: String) {
	File(rootDir, if(root == null) of else "${root.replace(':', '/')}/$of").list { dir, name ->
		File(dir, "$name/build.gradle.kts").exists()
	}!!.forEach { include(if(root == null) ":$of:$it" else ":$root:$of:$it") }
}

includeSubprojectsOf(of = "ide")
includeSubprojectsOf(root = "ide", of = "platform")
includeSubprojectsOf(root = "ide", of = "main")
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
