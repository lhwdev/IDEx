import com.idex.build.*

plugins {
	kotlin("multiplatform")
	kotlin("plugin.serialization")
	
	id("common-plugin")
}

repositories {
	maven("https://dl.bintray.com/mrasterisco/Maven")
}

kotlin {
	idexLibrary()
	
	dependencies {
		implementation(project(":ide:diff"))
		implementation(project(":ide:util"))
		
		implementation("io.github.mrasterisco:SortedList-common:1.2.0")
		
		implementation(coroutinesCore)
		implementation(serializationCore)
		implementation(serializationJson)
		implementation(io)
	}
}
