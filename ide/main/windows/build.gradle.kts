import com.idex.build.*
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
	kotlin("jvm")
	id("org.jetbrains.compose")
	
	id("common-plugin")
}

version = idexVersion

kotlin {
	setup()
}

dependencies {
	implementation(project(":ide:app"))
	implementation(project(":ide:platform-common"))
	implementation(project(":ide:platform:windows"))
	
	implementation(compose.desktop.windows_x64)
}


compose.desktop {
	application {
		mainClass = "com.idex.app.MainKt"
		
		nativeDistributions {
			packageVersion = idexPackageVersion
			targetFormats(TargetFormat.Exe)
		}
	}
}
