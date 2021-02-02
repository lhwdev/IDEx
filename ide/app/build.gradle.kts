import com.lhwdev.build.*
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

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
		
		implementation(compose.runtime)
		implementation(compose.foundation)
	}
}


compose.desktop {
	application {
		mainClass = "com.idex.app.DesktopMainKt"
		
		nativeDistributions {
			targetFormats(TargetFormat.Dmg, TargetFormat.Exe, TargetFormat.Deb)
			packageName = "IDEx"
		}
	}
}
