import com.idex.build.*

plugins {
	kotlin("multiplatform")
	id("org.jetbrains.compose")
	
	id("common-plugin")
}

kotlin {
	setupJvm("desktop") {
		dependencies {
			implementation(compose.desktop.currentOs)
		}
	}
	
	dependencies {
		api(compose.material)
		implementation(compose.runtime)
		implementation(compose.foundation)
	}
}
