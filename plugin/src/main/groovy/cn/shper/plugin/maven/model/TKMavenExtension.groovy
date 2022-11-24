package cn.shper.plugin.maven.model

import cn.shper.plugin.core.util.StringUtils
import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.internal.reflect.Instantiator
import org.gradle.api.publish.maven.internal.publication.*

/**
 * Author: shper
 * Version: V0.1 2019-07-10
 */
class TKMavenExtension {

    Boolean debug = false

    Boolean signing = false

    Boolean gradleModuleMetadata = false

    String groupId

    String artifactId

    String version

    final NamedDomainObjectContainer<TKFlavorExtension> flavors

    TKRepositoryExtension repository

    TKRepositoryExtension snapshotRepository

    DefaultMavenPom pom

    TKMavenExtension(Project project,
                     Instantiator instantiator,
                     NamedDomainObjectContainer<TKFlavorExtension> flavors) {
        this.flavors = flavors

        this.repository = instantiator.newInstance(TKRepositoryExtension.class)
        this.snapshotRepository = instantiator.newInstance(TKRepositoryExtension.class)

        this.pom = instantiator.newInstance(DefaultMavenPom.class,
            null,
            instantiator,
            project.objects)
    }

    void flavors(Action<? super NamedDomainObjectContainer<TKFlavorExtension>> action) {
        action.execute(flavors)
    }

    void repository(Action<TKRepositoryExtension> action) {
        action.execute(repository)
    }

    void snapshotRepository(Action<TKRepositoryExtension> action) {
        action.execute(snapshotRepository)
    }

    void pom(Action<DefaultMavenPom> action) {
        action.execute(pom)
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
                !(snapshotRepository && StringUtils.isNotNullAndNotEmpty(snapshotRepository.url))) {
            extensionError += "Missing repository. "
        }

        if (extensionError) {
            String prefix = "Have you created the TKMaven's extension? "
            throw new IllegalStateException(prefix + extensionError)
        }
    }

}