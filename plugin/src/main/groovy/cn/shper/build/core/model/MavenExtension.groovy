package cn.shper.build.core.model

import cn.shper.build.core.util.StringUtils
import org.gradle.api.Action
import org.gradle.internal.reflect.Instantiator

/**
 * Author: shper
 * Version: V0.1 2019-07-10
 */
class MavenExtension {

    String groupId
    String artifactId
    String version

    MavenRepositoryExtension repository
    MavenRepositoryExtension snapshotRepository
    MavenBintrayExtension bintray

    MavenExtension(Instantiator instantiator) {
        repository = instantiator.newInstance(MavenRepositoryExtension.class)
        snapshotRepository = instantiator.newInstance(MavenRepositoryExtension.class)
        bintray = instantiator.newInstance(MavenBintrayExtension.class)
    }

    void repository(Action<MavenRepositoryExtension> action) {
        action.execute(repository)
    }

    void snapshotRepository(Action<MavenRepositoryExtension> action) {
        action.execute(snapshotRepository)
    }

    void bintray(Action<MavenBintrayExtension> action) {
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
            String prefix = "Have you created the Shper maven? "
            throw new IllegalStateException(prefix + extensionError)
        }
    }

}