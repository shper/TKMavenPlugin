package cn.shper.plugin.maven.model

import cn.shper.plugin.core.util.StringUtils
import cn.shper.plugin.maven.model.ability.Artifactable

/**
 * Author: shper
 * Version: V0.1 2019-07-10
 */
class TKMavenBintrayExtension extends Artifactable {

    String user
    String apiKey

    String repo
    String userOrg

    String name

    String desc

    Map<String, String> versionAttributes = [:]

    String[] licences = ['Apache-2.0']

    String websiteUrl = ''
    String issueTrackerUrl = ''
    String vcsUrl = ''

    boolean publish = true

    boolean dryRun = false
    boolean override = false

    String[] publications

    boolean validate() {
        if (StringUtils.isNullOrEmpty(repo) || StringUtils.isNullOrEmpty(name) || StringUtils.isNullOrEmpty(desc)) {
            return false
        }

        return true
    }
}