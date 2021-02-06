import com.idex.build.*

plugins {
	kotlin("multiplatform")
	
	id("common-plugin")
}

kotlin {
	idexLibrary()
}
