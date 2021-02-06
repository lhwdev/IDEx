import com.idex.build.*

plugins {
	kotlin("multiplatform")
	id("org.jetbrains.compose")
	
	id("common-plugin")
}

kotlin {
	idexLibrary()
	
	dependencies {
		implementation(project(":ide:text"))
		
		implementation(compose.runtime)
		implementation(compose.foundation)
	}
}
