package cn.shper.plugin.maven

import cn.shper.plugin.core.util.StringUtils
import cn.shper.plugin.core.base.BasePlugin
import cn.shper.plugin.maven.attachment.AndroidAttachments
import cn.shper.plugin.maven.attachment.JavaAttachments
import cn.shper.plugin.maven.config.BintrayConfiguration
import cn.shper.plugin.maven.model.ZMavenExtension
import cn.shper.plugin.maven.model.ZMavenRepositoryExtension
import cn.shper.plugin.maven.model.ability.Artifactable
import com.android.build.gradle.api.LibraryVariant
import com.jfrog.bintray.gradle.BintrayPlugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.publish.PublicationContainer
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication

/**
 * Author: shper
 * Version: V0.1 2019-07-10
 */
class ZMavenPlugin extends BasePlugin {

    private Properties local
    private ZMavenExtension zMavenExtension

    @Override
    void subApply(Project project) {
        this.zMavenExtension = project.extensions.findByName("zmaven")
        if (!zMavenExtension) {
            this.zMavenExtension = project.extensions.create("zmaven", ZMavenExtension.class, instantiator)
        }

        createLocalProperties()

        project.afterEvaluate {
            zMavenExtension.validate()

            createPublishing(zMavenExtension)

            createBintrayPublishing(zMavenExtension)
        }

        project.apply([plugin: 'maven-publish'])
        new BintrayPlugin().apply(project)
    }

    private void createLocalProperties() {
        local = new Properties()
        // 如果文件不存在，不进行读取
        if (project.rootProject.file('local.properties').exists()) {
            local.load(project.rootProject.file('local.properties').newDataInputStream())
        }
    }

    private void createPublishing(ZMavenExtension mavenExtension) {
        project.publishing {
        }

        if (mavenExtension.repository && StringUtils.isNotNullAndNotEmpty(mavenExtension.repository.url)) {
            createRepository("", mavenExtension.repository)
            attachArtifacts(mavenExtension, mavenExtension.repository, "maven", false)
        }

        if (mavenExtension.snapshotRepository && StringUtils.isNotNullAndNotEmpty(mavenExtension.snapshotRepository.url)) {
            createRepository("snapshot", mavenExtension.snapshotRepository)
            attachArtifacts(mavenExtension, mavenExtension.snapshotRepository, "snapshot", true)
        }
    }

    private void createBintrayPublishing(ZMavenExtension mavenExtension) {
        if (!mavenExtension.bintray || !mavenExtension.bintray.validate()) {
            return
        }

        BintrayConfiguration.configure(project, local, mavenExtension)
        attachArtifacts(mavenExtension, mavenExtension.bintray, "bintray", false)

        createShperPublishTask("publishBintray", project.tasks.bintrayUpload)
    }

    private void attachArtifacts(ZMavenExtension mavenExtension,
                                 Artifactable anInterface,
                                 String namePrefix,
                                 boolean isSnapshot) {

        project.plugins.withId("com.android.library") {
            project.android.libraryVariants.all { LibraryVariant variant ->
                String name = namePrefix + StringUtils.toUpperCase(variant.name, 1)
                MavenPublication publication = createPublication(isSnapshot, name, mavenExtension)
                new AndroidAttachments(name, project, variant, anInterface).attachTo(publication)

                if (!namePrefix.equals("bintray")) {
                    createShperPublishTaskByName(name)
                }
            }
        }

        project.plugins.withId("java") {
            MavenPublication publication = createPublication(isSnapshot, namePrefix, mavenExtension)
            new JavaAttachments(namePrefix, project, anInterface).attachTo(publication)

            if (!namePrefix.equals("bintray")) {
                createShperPublishTaskByName(namePrefix)
            }
        }
    }

    private MavenPublication createPublication(boolean isSnapshot,
                                               String name,
                                               ZMavenExtension extension) {

        String groupId = extension.groupId
        String artifactId = extension.artifactId

        String version = extension.version
        if (isSnapshot && !version.endsWith("-SNAPSHOT")) {
            version += "-SNAPSHOT"
        }

        PublicationContainer publicationContainer = project.extensions.getByType(PublishingExtension.class).publications
        return publicationContainer.create(name, MavenPublication) { MavenPublication publication ->
            publication.groupId = groupId
            publication.artifactId = artifactId
            publication.version = version
        } as MavenPublication
    }

    private void createRepository(String alias,
                                  ZMavenRepositoryExtension extension) {

        if (StringUtils.isNullOrEmpty(extension.url)) {
            return
        }

        project.extensions.getByType(PublishingExtension.class).repositories.maven {
            name = alias
            url = extension.url

            if (extension.auth) {
                credentials {
                    // 优先从命令行中获取配置
                    username = project.hasProperty("userName")
                            ? project.property("userName")
                            : (StringUtils.isNotNullAndNotEmpty(extension.userName)
                            ? extension.userName
                            : local.getProperty("z-maven.userName"))

                    password = project.hasProperty("password")
                            ? project.property("password")
                            : (StringUtils.isNotNullAndNotEmpty(extension.password)
                            ? extension.password
                            : local.getProperty("z-maven.password"))
                }
            }
        }
    }

    private void createShperPublishTaskByName(String name) {
        String taskName = "publish" + StringUtils.toUpperCase(name, 1)
        createShperPublishTask(taskName, project.tasks.getByName(taskName + "PublicationToMavenRepository"))
    }

    private void createShperPublishTask(String name, Object object) {
        project.tasks.create(name) { Task task ->
            task.setGroup("ZMaven")
            task.dependsOn(object)
        }
    }

}