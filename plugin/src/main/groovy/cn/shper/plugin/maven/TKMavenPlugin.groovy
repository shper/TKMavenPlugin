package cn.shper.plugin.maven

import cn.shper.plugin.core.util.StringUtils
import cn.shper.plugin.core.base.BasePlugin
import cn.shper.plugin.maven.attachment.AndroidAttachments
import cn.shper.plugin.maven.attachment.JavaAttachments
import cn.shper.plugin.maven.config.BintrayConfiguration
import cn.shper.plugin.maven.model.TKMavenExtension
import cn.shper.plugin.maven.model.TKMavenRepositoryExtension
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
class TKMavenPlugin extends BasePlugin {

    private static final String KEY_EXTENSION_NAME = "tkmaven"

    private static final String KEY_USER_NAME = "userName"
    private static final String KEY_PASSWORD = "password"
    private static final String KEY_MAVEN_USER_NAME = "tk-maven.userName"
    private static final String KEY_MAVEN_PASSWORD = "tk-maven.password"

    private Properties local
    private TKMavenExtension tkMavenExtension

    @Override
    void subApply(Project project) {
        this.tkMavenExtension = project.extensions.findByName(KEY_EXTENSION_NAME)
        if (!tkMavenExtension) {
            this.tkMavenExtension = project.extensions.create(KEY_EXTENSION_NAME, TKMavenExtension.class, instantiator)
        }

        createLocalProperties()

        project.afterEvaluate {
            tkMavenExtension.validate()

            createPublishing(tkMavenExtension)

            createBintrayPublishing(tkMavenExtension)
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

    private void createPublishing(TKMavenExtension mavenExtension) {
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

    private void createBintrayPublishing(TKMavenExtension mavenExtension) {
        if (!mavenExtension.bintray || !mavenExtension.bintray.validate()) {
            return
        }

        BintrayConfiguration.configure(project, local, mavenExtension)
        attachArtifacts(mavenExtension, mavenExtension.bintray, "bintray", false)

        createShperPublishTask("publishBintray", project.tasks.bintrayUpload)
    }

    private void attachArtifacts(TKMavenExtension mavenExtension,
                                 Artifactable anInterface,
                                 String namePrefix,
                                 boolean isSnapshot) {

        project.plugins.withId("com.android.library") {
            project.android.libraryVariants.all { LibraryVariant variant ->
                String name = namePrefix + StringUtils.toUpperCase(variant.name, 1)
                MavenPublication publication = createPublication(isSnapshot, name, mavenExtension)
                new AndroidAttachments(name, project, variant, anInterface).attachTo(publication)

                if (namePrefix != "bintray") {
                    createShperPublishTaskByName(name, isSnapshot)
                }
            }
        }

        project.plugins.withId("java") {
            MavenPublication publication = createPublication(isSnapshot, namePrefix, mavenExtension)
            new JavaAttachments(namePrefix, project, anInterface).attachTo(publication)

            if (namePrefix != "bintray") {
                createShperPublishTaskByName(namePrefix, isSnapshot)
            }
        }
    }

    private MavenPublication createPublication(boolean isSnapshot,
                                               String name,
                                               TKMavenExtension extension) {
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
                                  TKMavenRepositoryExtension extension) {

        if (StringUtils.isNullOrEmpty(extension.url)) {
            return
        }

        project.extensions.getByType(PublishingExtension.class).repositories.maven {
            name = alias
            url = extension.url

            if (getMavenUserName(extension) != null && getMavenPassword(extension) != null) {
                credentials {
                    username = getMavenUserName(extension)
                    password = getMavenPassword(extension)
                }
            }
        }
    }

    private String getMavenUserName(TKMavenRepositoryExtension extension) {
        // 获取配置优先级为：命令行，其次 extension，再 local.properties，再 ~/.gradle/gradle.properties
        if (project.hasProperty(KEY_USER_NAME)) {
            return project.property(KEY_USER_NAME)
        }

        if (StringUtils.isNotNullAndNotEmpty(extension.userName)) {
            return extension.userName
        }

        if (local.getProperty(KEY_MAVEN_USER_NAME, null) != null) {
            return local.getProperty(KEY_MAVEN_USER_NAME)
        }

        if (project.hasProperty(KEY_MAVEN_USER_NAME)) {
            return project.property(KEY_MAVEN_USER_NAME)
        }

        return null
    }

    private String getMavenPassword(TKMavenRepositoryExtension extension) {
        // 获取配置优先级为：命令行，其次 extension，再 local.properties，再 ~/.gradle/gradle.properties
        if (project.hasProperty(KEY_PASSWORD)) {
            return project.property(KEY_PASSWORD)
        }

        if (StringUtils.isNotNullAndNotEmpty(extension.password)) {
            return extension.password
        }

        if (local.getProperty(KEY_MAVEN_PASSWORD, null) != null) {
            return local.getProperty(KEY_MAVEN_PASSWORD)
        }

        if (project.hasProperty(KEY_MAVEN_PASSWORD)) {
            return project.property(KEY_MAVEN_PASSWORD)
        }

        return null
    }

    private void createShperPublishTaskByName(String name, boolean isSnapshot) {
        String taskName = "publish" + StringUtils.toUpperCase(name, 1)
        String nameSuffix
        if (isSnapshot) {
            nameSuffix = "PublicationToSnapshotRepository"
        } else {
            nameSuffix = "PublicationToMavenRepository"
        }

        createShperPublishTask(taskName, project.tasks.getByName(taskName + nameSuffix))
    }

    private void createShperPublishTask(String name, Object object) {
        project.tasks.create(name) { Task task ->
            task.setGroup(KEY_EXTENSION_NAME)
            task.dependsOn(object)
        }
    }

}