import com.idex.build.*

plugins {
	kotlin("jvm")
	
	id("common-plugin")
}

kotlin {
	setup()
}

dependencies {
	implementation("net.java.dev.jna:jna:5.6.0")
	implementation("net.java.dev.jna:jna-platform:5.6.0")
}
