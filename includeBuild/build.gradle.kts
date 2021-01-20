plugins {
	`kotlin-dsl`
	`java-gradle-plugin`
}


group = "com.lhwdev.vrcar.include-build"
version = "SNAPSHOT"

repositories {
	mavenCentral()
}

gradlePlugin {
	plugins.register("common-plugin") {
		id = "common-plugin"
		implementationClass = "com.lhwdev.build.CommonPlugin"
	}
}

dependencies {
	val kotlinVersion = "1.4.21"
	implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
}
