// projects

include(":idex-plugin-gradle-plugin")

includeBuild("../includeBuild")


// plugins

pluginManagement {
	repositories {
		gradlePluginPortal()
		maven("https://maven.pkg.jetbrains.space/public/p/compose/dev") // Jetpack compose(org.jetbrains.compose)
	}
}
