import com.idex.build.*

plugins {
	kotlin("jvm")
	id("org.jetbrains.compose")
	
	id("common-plugin")
}

kotlin {
	setup()
}

dependencies {
	implementation(project(":ide:platform-common"))
	implementation(project(":ide:app"))
	
	implementation("net.java.dev.jna:jna:5.6.0")
	implementation("net.java.dev.jna:jna-platform:5.6.0")
	
	implementation(compose.desktop.windows_x64)
}

// tasks.withType<JavaCompile> {
// 	options.compilerArgs.addAll(listOf(
// 		"--add-exports", "java.desktop/java.awt.peer=ALL-UNNAMED",
// 		"--add-exports", "java.desktop/sun.java2d.pipe=ALL-UNNAMED"
// 	))
// 	options.isFork = true
// 	options.forkOptions.executable = "javac"
// }
