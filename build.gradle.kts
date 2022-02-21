// versions

plugins {
	val composeVersion = "0.4.0-build182"
	val kotlinVersion = "1.4.32" // also in root dependencies.kt in includeBuild
	
	kotlin("multiplatform") version kotlinVersion apply false
	kotlin("jvm") version kotlinVersion apply false
	kotlin("plugin.serialization") version kotlinVersion apply false
	
	id("org.jetbrains.compose") version composeVersion apply false
}


allprojects {
	repositories {
		mavenCentral()
		google()
		jcenter()
		maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
		maven("https://dl.bintray.com/mrasterisco/Maven")
	}
}
