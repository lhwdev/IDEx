plugins {
	kotlin("jvm")
	
	id("com.idex.plugin.gradle-plugin")
}

kotlin {
	dependencies {
		implementation(project(":ide:plugin"))
	}
}


idexPlugin {
	this.plugins {
		register("com.idex.my-plugin") {
			label = "My Plugin"
		}
	}
}
