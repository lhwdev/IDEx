import com.idex.build.*

plugins {
	kotlin("multiplatform")
	id("org.jetbrains.compose")
	
	id("common-plugin")
}

kotlin {
	idexLibrary()
	
	dependencies {
		implementation(project(":ide:ui-util"))
		implementation(project(":ide:ui-style"))
		
		implementation(compose.runtime)
		implementation(compose.foundation)
	}
}

dependencies {
	kotlinCompilerPluginClasspath(files("D:\\LHW\\develop\\kotlin\\compiler-plugin\\dump-ir\\compiler-plugin\\build\\libs\\compiler-plugin.jar"))
}
