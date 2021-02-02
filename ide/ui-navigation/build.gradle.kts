import com.lhwdev.build.*

plugins {
	kotlin("multiplatform")
	id("org.jetbrains.compose")
	
	id("common-plugin")
}

kotlin {
	setupJvm()
	
	dependencies {
		implementation(project(":ide:util"))
		implementation(project(":ide:ui-util"))
		implementation(compose.runtime)
	}
}
