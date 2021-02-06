package com.idex.plugin.gradle

import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Plugin
import org.gradle.api.Project


private const val sExtensionName = "idexPlugin"


class IdexPlugin : Plugin<Project> {
	private var extension: IdexPluginExtension? = null
	
	override fun apply(target: Project) {
		val extension = extension ?: IdexPluginExtension(target)
		this.extension = extension
		target.extensions.add(sExtensionName, extension)
	}
}

@Suppress("UnstableApiUsage")
open class IdexPluginExtension(project: Project) {
	val plugins: NamedDomainObjectContainer<IdexPluginItem> =
		project.objects.domainObjectContainer(IdexPluginItem::class.java)
}

open class IdexPluginItem(val name: String) {
	var label: String? = null
	var author: String? = null
	var description: String? = null
}
