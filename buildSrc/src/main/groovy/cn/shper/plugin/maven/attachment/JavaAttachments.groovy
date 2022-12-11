package cn.shper.plugin.maven.attachment

import cn.shper.plugin.maven.model.ability.Artifactable
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.component.SoftwareComponent
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.compile.GroovyCompile
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.javadoc.Javadoc

/**
 * Author: shper
 * Version: V0.1 2019-07-10
 */
class JavaAttachments extends MavenAttachments {

    protected SoftwareComponent softwareComponent

    JavaAttachments(String name, Project project, Artifactable artifactable) {
        super(project)

        this.softwareComponent = javaComponentFrom()

        if (artifactable.sourcesJar) {
            addArtifactSources(javaSourcesJarTask(project, name))
        }

        if (artifactable.javadocJar) {
            addArtifactSources(javaJavadocsJarTask(project, name))
        }
    }

    @Override
    void attachTo(MavenPublication publication) {
        super.attachTo(publication)

        publication.from softwareComponent
    }

    private SoftwareComponent javaComponentFrom() {
        return project.components.getByName('java')
    }

    private static Task javaSourcesJarTask(Project project, String name) {
        def fileTreeList = new ArrayList<>()

        JavaCompile javaCompile = project.compileJava
        fileTreeList.add(javaCompile.source)

        try {
            GroovyCompile groovyCompile = project.compileGroovy
            fileTreeList.add(groovyCompile.source)
        } catch (Exception ignore) {
        }

        return sourcesJarTask(project, name, null, fileTreeList.toArray())
    }

    private static Task javaJavadocsJarTask(Project project, String name) {
        Javadoc javadoc = project.javadoc
        return javadocsJarTask(project, name, javadoc,null)
    }

}