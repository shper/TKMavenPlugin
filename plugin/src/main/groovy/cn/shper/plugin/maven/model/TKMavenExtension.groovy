package cn.shper.plugin.maven.model

import cn.shper.plugin.core.util.Logger
import cn.shper.plugin.core.util.StringUtils
import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.internal.reflect.Instantiator

/**
 * Author: shper
 * Version: V0.1 2019-07-10
 */
class TKMavenExtension {

    String groupId
    String artifactId
    String version

    Boolean debug = false

    final NamedDomainObjectContainer<TKMavenFlavorExtension> flavors

    TKMavenRepositoryExtension repository
    TKMavenRepositoryExtension snapshotRepository
    TKMavenBintrayExtension bintray

    TKMavenExtension(Instantiator instantiator,
                     NamedDomainObjectContainer<TKMavenFlavorExtension> flavors) {
        this.flavors = flavors

        this.repository = instantiator.newInstance(TKMavenRepositoryExtension.class)
        this.snapshotRepository = instantiator.newInstance(TKMavenRepositoryExtension.class)
        this.bintray = instantiator.newInstance(TKMavenBintrayExtension.class)
    }

    void flavors(Action<? super NamedDomainObjectContainer<TKMavenFlavorExtension>> action) {
        action.execute(flavors)
    }

    void repository(Action<TKMavenRepositoryExtension> action) {
        action.execute(repository)
    }

    void snapshotRepository(Action<TKMavenRepositoryExtension> action) {
        action.execute(snapshotRepository)
    }

    void bintray(Action<TKMavenBintrayExtension> action) {
        action.execute(bintray)
    }

    void validate() {
        String extensionError = ""
        if (StringUtils.isNullOrEmpty(groupId)) {
            extensionError += "Missing groupId. "
        }

        if (StringUtils.isNullOrEmpty(artifactId)) {
            extensionError += "Missing artifactId. "
        }

        if (StringUtils.isNullOrEmpty(version)) {
            extensionError += "Missing version. "
        }

        if (!(repository && StringUtils.isNotNullAndNotEmpty(repository.url)) &&
                !(snapshotRepository && StringUtils.isNotNullAndNotEmpty(snapshotRepository.url)) &&
                !(bintray && bintray.validate())) {
            extensionError += "Missing repository. "
        }

        if (extensionError) {
            String prefix = "Have you created the TKMaven's extension? "
            throw new IllegalStateException(prefix + extensionError)
        }
    }

}