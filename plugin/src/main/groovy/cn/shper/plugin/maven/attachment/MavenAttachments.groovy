package cn.shper.plugin.maven.attachment

import cn.shper.plugin.core.util.CollectionUtils
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.component.SoftwareComponent
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.javadoc.Javadoc

/**
 * Author: shper
 * Version: V0.1 2019-07-17
 */
class MavenAttachments {

    private final SoftwareComponent softwareComponent
    private List<Object> allArtifactSources

    MavenAttachments(SoftwareComponent softwareComponent) {
        this.softwareComponent = softwareComponent
    }

    void addArtifactSources(Object artifactSource) {
        if (!this.allArtifactSources) {
            this.allArtifactSources = new ArrayList<>()
        }
        this.allArtifactSources.add(artifactSource)
    }

    final void attachTo(MavenPublication publication) {
        if (CollectionUtils.isNotNullAndNotEmpty(allArtifactSources)) {
            allArtifactSources.each {
                publication.artifact it
            }
        }

        publication.from softwareComponent
    }

    protected static Task sourcesJarTask(Project project, String publicationName, def ... sourcePaths) {
        return project.task("sourcesJarFor${publicationName.capitalize()}", type: Jar) { Jar jar ->
            jar.archiveClassifier.set("sources")
            jar.from sourcePaths
        }
    }

    protected static Task javadocsJarTask(Project project, String publicationName, Javadoc javadoc) {
        return project.task("javadocsJarFor${publicationName.capitalize()}", type: Jar) { Jar jar ->
            jar.archiveClassifier.set("javadoc")
            jar.exclude("**/*.kt")
            jar.from project.files(javadoc)
        }
    }

}