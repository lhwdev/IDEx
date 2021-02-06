import com.idex.build.*

plugins {
	kotlin("jvm")
	
	id("common-plugin")
}

kotlin {
	setup()
}

dependencies {
	implementation(project(":ide:app"))
	implementation(project(":ide:platform:windows"))
}
