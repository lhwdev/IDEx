import com.idex.build.*

plugins {
	kotlin("multiplatform")
	id("org.jetbrains.compose")
	
	id("common-plugin")
}

kotlin {
	setupJvm("desktop") {
		// target.withJava()
		
		dependencies {
			implementation(compose.desktop.currentOs)
		}
	}
	
	dependencies {
		implementation(project(":ide:ui-util"))
		implementation(project(":ide:ui-navigation"))
		implementation(project(":ide:ui-style"))
		implementation(project(":ide:ui-editor"))
		
		implementation(compose.runtime)
		implementation(compose.foundation)
	}
}
