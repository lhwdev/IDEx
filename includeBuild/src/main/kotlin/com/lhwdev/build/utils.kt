package com.lhwdev.build

import org.gradle.kotlin.dsl.invoke
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler
import org.jetbrains.kotlin.gradle.targets.js.dsl.KotlinJsTargetDsl
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget


fun KotlinMultiplatformExtension.dependencies(name: String = "commonMain", block: KotlinDependencyHandler.() -> Unit) {
	sourceSets {
		named(name) {
			dependencies(block)
		}
	}
}

fun KotlinProjectExtension.setupCommon() {
	sourceSets {
		all {
			languageSettings.apply {
				enableLanguageFeature("InlineClasses")
				useExperimentalAnnotation("kotlin.RequiresOptIn")
				useExperimentalAnnotation("kotlin.ExperimentalUnsignedTypes")
				// useExperimentalAnnotation("kotlin.io.path.ExperimentalPathApi")
			}
		}
		
		val testSourceSet = when(this@setupCommon) {
			is KotlinMultiplatformExtension -> "commonTest"
			else -> "test"
		}
		named(testSourceSet) {
			dependencies {
				implementation(kotlin("test-common"))
				implementation(kotlin("test-annotations-common"))
			}
		}
	}
}

fun KotlinMultiplatformExtension.setupJvm(name: String = "jvm", configure: (KotlinJvmTarget.() -> Unit)? = null) {
	jvm(name) {
		compilations.all {
			kotlinOptions.jvmTarget = "1.8"
		}
		
		configure?.invoke(this)
	}
	
	setupJvmCommon(name)
}

fun KotlinJvmProjectExtension.setup() {
	setupCommon()
	setupJvmCommon(null)
	
	target.compilations.all {
		kotlinOptions.jvmTarget = "1.8"
	}
}

private fun KotlinProjectExtension.setupJvmCommon(name: String?) {
	sourceSets {
		named(if(name == null) "test" else "${name}Test") {
			dependencies {
				implementation(kotlin("test-junit"))
			}
		}
	}
}

fun KotlinMultiplatformExtension.setupJs(name: String = "js", configure: (KotlinJsTargetDsl.() -> Unit)? = null) {
	js(name) {
		configure?.invoke(this)
	}
	
	sourceSets {
		named("${name}Test") {
			dependencies {
				implementation(kotlin("test-js"))
			}
		}
	}
}


fun KotlinMultiplatformExtension.idexLibrary() {
	setupJvm()
	// js {
	// 	browser()
	// 	nodejs()
	// }
	
	// val hostOs = System.getProperty("os.name")
	// val isMingwX64 = hostOs.startsWith("Windows")
	// when {
	// 	hostOs == "Mac OS X" -> macosX64("native")
	// 	hostOs == "Linux" -> linuxX64("native")
	// 	isMingwX64 -> mingwX64("native")
	// 	else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
	// }
}
