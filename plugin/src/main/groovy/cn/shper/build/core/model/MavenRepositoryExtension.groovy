package cn.shper.build.core.model

import cn.shper.build.core.model.ability.Artifactable

/**
 * Author: shper
 * Version: V0.1 2019-07-10
 */
class MavenRepositoryExtension extends Artifactable {

    String url

    boolean auth = true

    String userName

    String password

}