package cn.shper.plugin.maven.model

import org.gradle.api.NamedDomainObjectFactory
import org.gradle.internal.reflect.Instantiator

/**
 * Author: shper
 * Version: V0.1 2020-10-31
 */
class TKMavenFlavorFactory implements NamedDomainObjectFactory<TKMavenFlavorExtension> {

    final Instantiator instantiator

    TKMavenFlavorFactory(Instantiator instantiator) {
        this.instantiator = instantiator
    }

    @Override
    TKMavenFlavorExtension create(String name) {
        return instantiator.newInstance(TKMavenFlavorExtension.class, name)
    }
}