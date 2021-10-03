package cn.shper.plugin.maven

import cn.shper.plugin.core.util.StringUtils
import cn.shper.plugin.core.util.CollectionUtils
import cn.shper.plugin.core.base.BasePlugin
import cn.shper.plugin.maven.attachment.AndroidAttachments
import cn.shper.plugin.maven.attachment.JavaAttachments

import cn.shper.plugin.maven.model.TKMavenExtension
import cn.shper.plugin.maven.model.TKFlavorExtension
import cn.shper.plugin.maven.model.TKFlavorFactory
import cn.shper.plugin.maven.model.TKRepositoryExtension
import cn.shper.plugin.maven.model.ability.Artifactable
import com.android.build.gradle.api.LibraryVariant
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.publish.PublicationContainer
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPom
import org.gradle.api.publish.maven.MavenPomDeveloperSpec
import org.gradle.api.publish.maven.MavenPomLicenseSpec
import org.gradle.api.publish.maven.MavenPomScm
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.plugins.signing.SigningExtension

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

    private List<String> flavors = []
    final private Map<String, TKFlavorExtension> flavorExtensionMap = [:]

    @Override
    void subApply(Project project) {
        this.tkMavenExtension = project.extensions.findByName(KEY_EXTENSION_NAME)
        if (!tkMavenExtension) {

            def flavorContainer = project.container(TKFlavorExtension,
                    new TKFlavorFactory(instantiator))

            this.tkMavenExtension = project.extensions.create(KEY_EXTENSION_NAME,
                    TKMavenExtension.class,
                    project,
                    instantiator,
                    flavorContainer)

            flavorContainer.whenObjectAdded { TKFlavorExtension flavor ->
                flavorExtensionMap[flavor.name] = flavor
            }
        }

        createLocalProperties()

        project.afterEvaluate {
            tkMavenExtension.validate()

            createPublishing()
            createSigning()
        }

        project.apply([plugin: 'maven-publish'])
        project.apply([plugin: 'signing'])
    }

    private void createLocalProperties() {
        local = new Properties()
        // 如果文件不存在，不进行读取
        if (project.rootProject.file('local.properties').exists()) {
            local.load(project.rootProject.file('local.properties').newDataInputStream())
            local.each { name, value ->
                project.extensions.extraProperties.set(name, value)
            }
        }
    }

    private void createPublishing() {
        project.publishing {
        }

        if (tkMavenExtension.repository && StringUtils.isNotNullAndNotEmpty(tkMavenExtension.repository.url)) {
            createRepository("", tkMavenExtension.repository)
            attachArtifacts( tkMavenExtension.repository, "maven", false)
        }

        if (tkMavenExtension.snapshotRepository && StringUtils.isNotNullAndNotEmpty(tkMavenExtension.snapshotRepository.url)) {
            createRepository("snapshot", tkMavenExtension.snapshotRepository)
            attachArtifacts(tkMavenExtension.snapshotRepository, "snapshot", true)
        }
    }

    private void attachArtifacts(Artifactable anInterface,
                                 String namePrefix,
                                 boolean isSnapshot) {

        project.plugins.withId("com.android.library") {
            project.android.libraryVariants.all { LibraryVariant variant ->
                if (!tkMavenExtension.debug && variant.buildType.debuggable) {
                    return
                }

                String name = namePrefix + StringUtils.toUpperCase(variant.name, 1)

                String flavorName = variant.flavorName
                TKFlavorExtension flavorExtension = flavorExtensionMap.get(flavorName)

                if (StringUtils.isNotNullAndNotEmpty(flavorName) &&
                        !flavors.contains(StringUtils.toUpperCase(flavorName, 1))) {
                    flavors.add(StringUtils.toUpperCase(flavorName, 1))
                }

                MavenPublication publication = createPublication(isSnapshot, name, flavorExtension)
                new AndroidAttachments(name, project, variant, anInterface).attachTo(publication)

                createShperPublishTaskByName(name, isSnapshot)
            }
        }

        project.plugins.withId("java") {
            MavenPublication publication = createPublication(isSnapshot, namePrefix, null)
            new JavaAttachments(namePrefix, project, anInterface).attachTo(publication)

            createShperPublishTaskByName(namePrefix, isSnapshot)
        }
    }

    private MavenPublication createPublication(boolean isSnapshot,
                                               String name,
                                               TKFlavorExtension flavorExtension) {

        String groupId = tkMavenExtension.groupId
        String artifactId = tkMavenExtension.artifactId

        String version = tkMavenExtension.version

        if (flavorExtension != null) {
            if (StringUtils.isNotNullAndNotEmpty(flavorExtension.groupIdSuffix)) {
                groupId += flavorExtension.groupIdSuffix
            }
            if (StringUtils.isNotNullAndNotEmpty(flavorExtension.groupId)) {
                groupId = flavorExtension.groupId
            }

            if (StringUtils.isNotNullAndNotEmpty(flavorExtension.artifactIdSuffix)) {
                artifactId += flavorExtension.artifactIdSuffix
            }
            if (StringUtils.isNotNullAndNotEmpty(flavorExtension.artifactId)) {
                artifactId = flavorExtension.artifactId
            }

            if (StringUtils.isNotNullAndNotEmpty(flavorExtension.versionSuffix)) {
                version += flavorExtension.versionSuffix
            }
            if (StringUtils.isNotNullAndNotEmpty(flavorExtension.version)) {
                version = flavorExtension.version
            }
        }

        if (isSnapshot && !version.endsWith("-SNAPSHOT")) {
            version += "-SNAPSHOT"
        }

        PublicationContainer publicationContainer = project.extensions.getByType(PublishingExtension.class).publications
        return publicationContainer.create(name, MavenPublication) { MavenPublication publication ->
            publication.groupId = groupId
            publication.artifactId = artifactId
            publication.version = version

            applyMavenPom(publication)

        } as MavenPublication
    }

    private void applyMavenPom(MavenPublication publication) {
        if (tkMavenExtension.pom != null) {
            publication.pom({ MavenPom pom ->
                if (tkMavenExtension.pom.name != null) {
                    pom.name.set(tkMavenExtension.pom.name)
                }
                if (tkMavenExtension.pom.description != null) {
                    pom.description.set(tkMavenExtension.pom.description)
                }
                if (tkMavenExtension.pom.url != null) {
                    pom.url.set(tkMavenExtension.pom.url)
                }
                if (CollectionUtils.isNotNullAndNotEmpty(tkMavenExtension.pom.licenses)) {
                    pom.licenses({ MavenPomLicenseSpec licenseSpec ->
                        for (license in tkMavenExtension.pom.licenses) {
                            licenseSpec.license({
                                if (license.name) {
                                    it.name.set(license.name)
                                }
                                if (license.url) {
                                    it.url.set(license.url)
                                }
                                if (license.distribution) {
                                    it.distribution.set(license.distribution)
                                }
                            })
                        }
                    })
                }

                if (tkMavenExtension.pom.developers) {
                    pom.developers({MavenPomDeveloperSpec developerSpec->
                        for (developer in tkMavenExtension.pom.developers) {
                            developerSpec.developer({
                                if (developer.id) {
                                    it.id.set(developer.id)
                                }
                                if (developer.name) {
                                    it.name.set(developer.name)
                                }
                                if (developer.email) {
                                    it.email.set(developer.email)
                                }
                            })
                        }
                    })
                }
                if (tkMavenExtension.pom.scm) {
                    pom.scm({ MavenPomScm scm ->
                        if (tkMavenExtension.pom.scm.connection) {
                            scm.connection.set(tkMavenExtension.pom.scm.connection)
                        }
                        if (tkMavenExtension.pom.scm.developerConnection) {
                            scm.developerConnection.set(tkMavenExtension.pom.scm.developerConnection)
                        }
                        if (tkMavenExtension.pom.scm.url) {
                            scm.url.set(tkMavenExtension.pom.scm.url)
                        }
                    })
                }
            })
        }
    }

    private void createRepository(String alias,
                                  TKRepositoryExtension extension) {

        if (StringUtils.isNullOrEmpty(extension.url)) {
            return
        }

        project.extensions.getByType(PublishingExtension.class).repositories.maven {
            name = alias
            url = extension.url

            if (extension.authable && getMavenUserName(extension) != null && getMavenPassword(extension) != null) {
                credentials {
                    username = getMavenUserName(extension)
                    password = getMavenPassword(extension)
                }
            }
        }
    }

    private void createSigning() {
        if (tkMavenExtension.signing) {
            project.signing {
            }

            project.extensions.getByType(SigningExtension.class).sign(project.extensions.getByType(PublishingExtension.class).publications)
        }
    }

    private String getMavenUserName(TKRepositoryExtension extension) {
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

    private String getMavenPassword(TKRepositoryExtension extension) {
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