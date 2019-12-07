package cn.shper.build.maven

import cn.shper.build.core.base.BasePlugin
import cn.shper.build.maven.attachment.AndroidAttachments
import cn.shper.build.maven.attachment.JavaAttachments
import cn.shper.build.maven.config.BintrayConfiguration
import cn.shper.build.core.util.StringUtils
import cn.shper.build.core.model.MavenExtension
import cn.shper.build.core.model.MavenRepositoryExtension
import cn.shper.build.core.model.ability.Artifactable
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
class MavenPlugin extends BasePlugin {

    private Properties local

    @Override
    void subApply(Project project) {
        def mavenExtension = shperExtension.getMaven()
        if (mavenExtension == null) {
            return
        }

        createLocalProperties()

        project.afterEvaluate {
            mavenExtension.validate()

            createPublishing(mavenExtension)

            createBintrayPublishing(mavenExtension)
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

    private void createPublishing(MavenExtension mavenExtension) {
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

    private void createBintrayPublishing(MavenExtension mavenExtension) {
        if (!mavenExtension.bintray || !mavenExtension.bintray.validate()) {
            return
        }

        BintrayConfiguration.configure(project, local, mavenExtension)
        attachArtifacts(mavenExtension, mavenExtension.bintray, "bintray", false)

        createShperPublishTask("bintrayPublish", project.tasks.bintrayUpload)
    }

    private void attachArtifacts(MavenExtension mavenExtension,
                                 Artifactable anInterface,
                                 String namePrefix,
                                 boolean isSnapshot) {

        project.plugins.withId('com.android.library') {
            project.android.libraryVariants.all { LibraryVariant variant ->
                String name = namePrefix + StringUtils.toUpperCase(variant.name, 1)
                MavenPublication publication = createPublication(isSnapshot, name, mavenExtension)
                new AndroidAttachments(name, project, variant, anInterface).attachTo(publication)

                createShperPublishTaskByName(name)
            }
        }

        project.plugins.withId('java') {
            MavenPublication publication = createPublication(isSnapshot, namePrefix, mavenExtension)
            new JavaAttachments(namePrefix, project, anInterface).attachTo(publication)

            createShperPublishTaskByName(namePrefix)
        }
    }

    private MavenPublication createPublication(boolean isSnapshot,
                                               String name,
                                               MavenExtension extension) {

        String groupId = extension.groupId
        String artifactId = extension.artifactId

        String version = extension.version
        if (isSnapshot && !version.endsWith("-SNAPSHOT")) {

            // 三段式版本号直接删除 最后一段应急版本号，修改为通用版本号 .X
            if (version.split("\\.").length > 2) {
                version = version.substring(0, version.lastIndexOf("."))
                version += ".X"
            }

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
                                  MavenRepositoryExtension extension) {

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
                            : local.getProperty("shper.maven.userName"))

                    password = project.hasProperty("password")
                            ? project.property("password")
                            : (StringUtils.isNotNullAndNotEmpty(extension.password)
                            ? extension.password
                            : local.getProperty("shper.maven.password"))
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
            task.setGroup("shper")
            task.dependsOn(object)
        }
    }

}