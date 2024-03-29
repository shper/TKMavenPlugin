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

    protected Project project
    protected List<Object> allArtifactSources

    MavenAttachments(Project project) {
        this.project = project
    }

    void addArtifactSources(Object artifactSource) {
        if (!this.allArtifactSources) {
            this.allArtifactSources = new ArrayList<>()
        }
        this.allArtifactSources.add(artifactSource)
    }

    void attachTo(MavenPublication publication) {
        if (CollectionUtils.isNotNullAndNotEmpty(allArtifactSources)) {
            allArtifactSources.each {
                publication.artifact it
            }
        }
    }

    protected static Task sourcesJarTask(Project project, String publicationName, List<String> excludes, def ... sourcePaths) {
        return project.task("sourcesJarFor${publicationName.capitalize()}", type: Jar) { Jar jar ->
            jar.archiveClassifier.set("sources")

            if (CollectionUtils.isNotNullAndNotEmpty(excludes)) {
              jar.exclude(excludes)
            }

            jar.from sourcePaths
        }
    }

    protected static Task javadocsJarTask(Project project, String publicationName, Javadoc javadoc, List<String> excludes) {
        return project.task("javadocsJarFor${publicationName.capitalize()}", type: Jar) { Jar jar ->
            jar.archiveClassifier.set("javadoc")

            if (excludes == null) {
                excludes = new ArrayList<String>()
            }
            excludes.add("**/*.kt")
            jar.exclude(excludes)

            jar.from project.files(javadoc)
        }
    }

}