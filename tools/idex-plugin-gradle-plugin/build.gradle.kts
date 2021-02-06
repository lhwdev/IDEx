import com.idex.build.*

plugins {
	`java-gradle-plugin`
	kotlin("jvm")
	
	id("common-plugin")
}

kotlin {
	setup()
}

gradlePlugin {
	plugins {
		create("IdexPlugin") {
			id = "com.idex.plugin.gradle-plugin"
			implementationClass = "com.idex.plugin.gradle.IdexPlugin"
		}
	}
}
