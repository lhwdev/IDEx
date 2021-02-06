// versions

plugins {
	val composeVersion = "0.3.0-build141"
	val kotlinVersion = "1.4.30"
	
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
	}
}
