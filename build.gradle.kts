buildscript {
	val composeVersion = "0.3.0-build135"
	val kotlinVersion = "1.4.21"
	
	
	repositories {
		mavenCentral()
		google()
		jcenter()
		maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
	}
	
	dependencies {
		classpath("org.jetbrains.compose:compose-gradle-plugin:$composeVersion")
		classpath(kotlin("gradle-plugin", version = kotlinVersion))
		classpath("org.jetbrains.kotlin:kotlin-serialization:$kotlinVersion")
	}
}

allprojects {
	repositories {
		mavenCentral()
		google()
		jcenter()
		maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
	}
}
