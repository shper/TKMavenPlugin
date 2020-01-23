package cn.shper.plugin.maven.model

import cn.shper.plugin.maven.model.ability.Artifactable

/**
 * Author: shper
 * Version: V0.1 2019-07-10
 */
class ZMavenRepositoryExtension extends Artifactable {

    String url

    boolean auth = true

    String userName

    String password

}