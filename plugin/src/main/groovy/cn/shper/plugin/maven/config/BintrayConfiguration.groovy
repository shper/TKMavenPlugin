package cn.shper.plugin.maven.config

import cn.shper.plugin.core.util.StringUtils
import cn.shper.plugin.maven.model.TKMavenBintrayExtension
import cn.shper.plugin.maven.model.TKMavenExtension
import org.gradle.api.Project

/**
 * Author: shper
 * Version: V0.1 2019-07-10
 */
class BintrayConfiguration {

    private static final String KEY_USER = "user"
    private static final String KEY_API = "apiKey"
    private static final String KEY_BINTRAY_USER = "tk-maven.bintray.user"
    private static final String KEY_BINTRAY_API = "tk-maven.bintray.apiKey"

    static void configure(Project project,
                          Properties local,
                          TKMavenExtension extension) {

        def bintrayExtension = extension.bintray

        project.bintray {
            user = getUser(project, local, bintrayExtension)
            key = getKey(project, local, bintrayExtension)

            publish = bintrayExtension.publish
            dryRun = bintrayExtension.dryRun
            override = bintrayExtension.override

            publications = bintrayExtension.publications
                    ?: project.plugins.hasPlugin('com.android.library')
                    ? ['bintrayRelease'] : ['bintray']

            pkg {
                repo = bintrayExtension.repo
                userOrg = bintrayExtension.userOrg
                name = bintrayExtension.name
                desc = bintrayExtension.desc
                websiteUrl = bintrayExtension.websiteUrl
                issueTrackerUrl = bintrayExtension.issueTrackerUrl
                vcsUrl = bintrayExtension.vcsUrl

                licenses = bintrayExtension.licences
                version {
                    name = extension.version
                    attributes = bintrayExtension.versionAttributes
                }
            }
        }

        project.tasks.bintrayUpload.mustRunAfter(project.tasks.uploadArchives)
    }

    private static String getUser(Project project,
                                  Properties local,
                                  TKMavenBintrayExtension bintrayExtension) {
        // 获取配置优先级为：命令行，其次 extension，再 local.properties，再 ~/.gradle/gradle.properties
        if (project.hasProperty(KEY_USER)) {
            return project.property(KEY_USER)
        }

        if (StringUtils.isNotNullAndNotEmpty(bintrayExtension.user)) {
            return bintrayExtension.user
        }

        if (local.getProperty(KEY_BINTRAY_USER, null) != null) {
            return local.getProperty(KEY_BINTRAY_USER)
        }

        if (project.hasProperty(KEY_BINTRAY_USER)) {
            return project.property(KEY_BINTRAY_USER)
        }

        return ""
    }

    private static String getKey(Project project,
                                 Properties local,
                                 TKMavenBintrayExtension bintrayExtension) {
        // 获取配置优先级为：命令行，其次 extension，再 local.properties，再 ~/.gradle/gradle.properties
        if (project.hasProperty(KEY_API)) {
            return project.property(KEY_API)
        }

        if (StringUtils.isNotNullAndNotEmpty(bintrayExtension.apiKey)) {
            return bintrayExtension.apiKey
        }

        if (local.getProperty(KEY_BINTRAY_API, null) != null) {
            return local.getProperty(KEY_BINTRAY_API)
        }

        if (project.hasProperty(KEY_BINTRAY_API)) {
            return project.property(KEY_BINTRAY_API)
        }

        return ""
    }

}