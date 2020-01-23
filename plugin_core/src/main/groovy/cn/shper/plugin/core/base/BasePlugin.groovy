package cn.shper.plugin.core.base

import com.android.build.gradle.AppExtension
import com.android.build.gradle.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.internal.reflect.Instantiator
import org.gradle.invocation.DefaultGradle

/**
 * Author: shper
 * Version: V0.1 2019-07-10
 */
abstract class BasePlugin implements Plugin<Project> {

    protected Project project
    protected Instantiator instantiator

    @Override
    void apply(Project project) {
        this.project = project
        this.instantiator = ((DefaultGradle) project.getGradle()).getServices().get(Instantiator.class)

        subApply(project)
    }

    abstract void subApply(Project project)

    protected final AppExtension getAndroid() {
        return this.project.android
    }

    protected final LibraryExtension getAndroidLib() {
        return this.project.android
    }

    protected final boolean isAndroidApplication() {
        return this.project.plugins.hasPlugin("com.android.application")
    }

    protected final boolean isAndroidLibrary() {
        return this.project.plugins.hasPlugin("com.android.library")
    }

    protected final boolean isJavaLibrary() {
        return this.project.plugins.hasPlugin("java-library")
    }

}