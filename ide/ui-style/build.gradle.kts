import com.idex.build.*

plugins {
	kotlin("multiplatform")
	id("org.jetbrains.compose")
	
	id("common-plugin")
}

kotlin {
	setupJvm()
	
	dependencies {
		implementation(compose.runtime)
		implementation(compose.foundation)
	}
}
