package cn.shper.plugin.maven.model

/**
 * Author: shper
 * Version: V0.1 2020-10-31
 */
class TKFlavorExtension {

    String name

    String groupId
    String groupIdSuffix

    String artifactId
    String artifactIdSuffix

    String version
    String versionSuffix

  TKFlavorExtension(String name) {
        this.name = name
    }

}