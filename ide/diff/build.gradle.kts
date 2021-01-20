import com.lhwdev.build.*

plugins {
	kotlin("multiplatform")
	id("kotlinx-serialization")
	
	id("common-plugin")
}

repositories {
	maven("https://dl.bintray.com/mrasterisco/Maven")
}

kotlin {
	idexLibrary()
	
	dependencies {
		implementation(project(":ide:util"))
		
		implementation("io.github.mrasterisco:SortedList-common:1.2.0")
		
		implementation(coroutinesCore)
		implementation(serializationCore)
		implementation(serializationJson)
		implementation(io)
	}
}
