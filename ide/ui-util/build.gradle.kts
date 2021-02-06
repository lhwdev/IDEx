import com.idex.build.*

plugins {
	kotlin("multiplatform")
	id("org.jetbrains.compose")
	
	id("common-plugin")
}

kotlin {
	idexLibrary()
	
	dependencies {
		implementation(project(":ide:util"))
		
		implementation("org.jetbrains.kotlinx:kotlinx-collections-immutable:0.3.3")
		
		implementation(compose.runtime)
		implementation(compose.foundation)
	}
}
