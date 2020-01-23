package cn.shper.plugin.core.utils

/**
 * Author: shper
 * Version: V0.1 2019-07-18
 */
class CollectionUtils {

    static boolean isNullOrEmpty(Collection collection) {
        return !collection || collection.size() < 1
    }

    static boolean isNotNullAndNotEmpty(Collection collection) {
        return !isNullOrEmpty(collection)
    }

}
