package cn.shper.plugin.maven.model

import cn.shper.plugin.core.util.StringUtils
import org.gradle.api.Action
import org.gradle.internal.reflect.Instantiator

/**
 * Author: shper
 * Version: V0.1 2019-07-10
 */
class ZMavenExtension {

    String groupId
    String artifactId
    String version

    ZMavenRepositoryExtension repository
    ZMavenRepositoryExtension snapshotRepository
    ZMavenBintrayExtension bintray

    ZMavenExtension(Instantiator instantiator) {
        repository = instantiator.newInstance(ZMavenRepositoryExtension.class)
        snapshotRepository = instantiator.newInstance(ZMavenRepositoryExtension.class)
        bintray = instantiator.newInstance(ZMavenBintrayExtension.class)
    }

    void repository(Action<ZMavenRepositoryExtension> action) {
        action.execute(repository)
    }

    void snapshotRepository(Action<ZMavenRepositoryExtension> action) {
        action.execute(snapshotRepository)
    }

    void bintray(Action<ZMavenBintrayExtension> action) {
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
            String prefix = "Have you created the ZMaven? "
            throw new IllegalStateException(prefix + extensionError)
        }
    }

}