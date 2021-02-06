plugins {
	`kotlin-dsl`
	`java-gradle-plugin`
}


group = "com.idex.include-build"
version = "SNAPSHOT"

repositories {
	mavenCentral()
}

gradlePlugin {
	plugins.register("common-plugin") {
		id = "common-plugin"
		implementationClass = "com.idex.build.CommonPlugin"
	}
}

dependencies {
	implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.30")
}
