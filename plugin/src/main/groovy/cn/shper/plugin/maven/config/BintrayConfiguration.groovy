package cn.shper.plugin.maven.config

import cn.shper.plugin.core.util.StringUtils
import cn.shper.plugin.maven.model.ZMavenExtension
import org.gradle.api.Project

class BintrayConfiguration {

    static void configure(Project project,
                          Properties local,
                          ZMavenExtension extension) {

        def bintrayExtension = extension.bintray

        project.bintray {
            // 优先从命令行中获取配置
            user = project.hasProperty("user")
                    ? project.property("user")
                    : (StringUtils.isNotNullAndNotEmpty(bintrayExtension.user) ?
                    bintrayExtension.user : local.getProperty("z-maven.bintray.user"))

            key = project.hasProperty("apiKey")
                    ? project.property("apiKey")
                    : (StringUtils.isNotNullAndNotEmpty(bintrayExtension.apiKey) ?
                    bintrayExtension.apiKey : local.getProperty("z-maven.bintray.apiKey"))

            publish = bintrayExtension.publish
            dryRun = bintrayExtension.dryRun
            override = bintrayExtension.override

            publications = bintrayExtension.publications ?: project.plugins.hasPlugin('com.android.library') ?
                    ['bintrayRelease'] : ['bintray']

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

}