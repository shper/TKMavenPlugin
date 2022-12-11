package cn.shper.plugin.maven.attachment

import cn.shper.plugin.core.util.CollectionUtils
import cn.shper.plugin.maven.model.ability.Artifactable
import com.android.build.gradle.api.LibraryVariant
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.javadoc.Javadoc

/**
 * Author: shper
 * Version: V0.1 2019-07-10
 */
class AndroidAttachments extends MavenAttachments {

    AndroidAttachments(String name, Project project, LibraryVariant variant, Artifactable artifactable) {
        super(project)

        if (artifactable.sourcesJar) {
            addArtifactSources(androidSourcesJarTask(project, name, variant))
        }

        if (artifactable.javadocJar) {
            addArtifactSources(androidJavadocsJarTask(project, name, variant))
        }
    }

    void attachTo(MavenPublication publication) {
        if (CollectionUtils.isNotNullAndNotEmpty(allArtifactSources)) {
            allArtifactSources.each {
                publication.artifact it
            }
        }

        project.afterEvaluate {
            publication.artifact(project.tasks.getByName("bundleReleaseAar"))
        }
    }

    private static Task androidSourcesJarTask(Project project, String publicationName, LibraryVariant variant) {
        def sourcePaths = variant.sourceSets.collect {
            it.javaDirectories
            it.kotlinDirectories
        }.flatten()

        def excludes = new ArrayList<String>()
        excludes.add("**/R.class")

        return sourcesJarTask(project, publicationName, excludes, sourcePaths)
    }

    private static Task androidJavadocsJarTask(Project project, String publicationName, LibraryVariant variant) {
        Javadoc javadoc = project.task("javadoc${publicationName.capitalize()}", type: Javadoc) { Javadoc javadoc ->
            javadoc.source = variant.javaCompileProvider.get().source
            javadoc.classpath = variant.javaCompileProvider.get().classpath
        } as Javadoc

        return javadocsJarTask(project, publicationName, javadoc,null)
    }

}