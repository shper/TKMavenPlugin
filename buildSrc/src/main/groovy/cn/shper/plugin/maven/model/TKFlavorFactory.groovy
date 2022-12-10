package cn.shper.plugin.maven.model

import org.gradle.api.NamedDomainObjectFactory
import org.gradle.internal.reflect.Instantiator

/**
 * Author: shper
 * Version: V0.1 2020-10-31
 */
class TKFlavorFactory implements NamedDomainObjectFactory<TKFlavorExtension> {

    final Instantiator instantiator

    TKFlavorFactory(Instantiator instantiator) {
        this.instantiator = instantiator
    }

    @Override
    TKFlavorExtension create(String name) {
        return instantiator.newInstance(TKFlavorExtension.class, name)
    }
}