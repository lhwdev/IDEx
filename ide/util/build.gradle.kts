import com.lhwdev.build.idexLibrary

plugins {
	kotlin("multiplatform")
	
	id("common-plugin")
}

kotlin {
	idexLibrary()
}
