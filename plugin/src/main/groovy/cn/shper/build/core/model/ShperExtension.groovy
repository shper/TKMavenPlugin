package cn.shper.build.core.model

import org.gradle.api.Action
import org.gradle.internal.reflect.Instantiator

/**
 * Author: shper
 * Version: V0.1 2019-07-10
 */
class ShperExtension {

    APKNameExtension apkName
    APKCopyExtension apkCopy
    MavenExtension maven

    ShperExtension(Instantiator instantiator) {
        apkName = instantiator.newInstance(APKNameExtension.class)
        apkCopy = instantiator.newInstance(APKCopyExtension.class)
        maven = instantiator.newInstance(MavenExtension.class, instantiator)
    }

    void apkName(Action<APKNameExtension> action) {
        action.execute(apkName)
    }

    void apkCopy(Action<APKCopyExtension> action) {
        action.execute(apkCopy)
    }

    void maven(Action<MavenExtension> action) {
        action.execute(maven)
    }

}
