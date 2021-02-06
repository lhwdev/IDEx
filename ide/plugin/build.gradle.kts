import com.idex.build.*

plugins {
	kotlin("multiplatform")
	kotlin("plugin.serialization")
	id("org.jetbrains.compose")
	
	id("common-plugin")
}

kotlin {
	idexLibrary()
	
	explicitApiWarning()
	
	dependencies {
		implementation(project(":ide:core"))
		implementation(compose.runtime)
		implementation(serializationCore)
	}
}
