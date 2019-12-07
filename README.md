# Shper ZZ-Plugins

## Sample usage

### Step 1

Add the Shper ZZ-Plugin to your `buildscript`:

```
buildscript {
    repositories {
        maven{ url 'https://dl.bintray.com/shper/maven' }
    }

    dependencies {
        // Shper ZZ-Plugin 编译工具
        classpath 'cn.shper.build:zz-plugin:1.0.0'
    }
}
```

### Step 2

Add the your username and password to your `local.properties`:


```
# Maven's username and password
shper.maven.userName=XXX
shper.maven.password=XXX

# Bintray's user and apikey
shper.maven.bintray.user=XXX
shper.maven.bintray.apiKey=XXX
```

### Step 3

Add the shper extension to your `build.gradle`:

```
apply plugin: 'shper-maven'
shper {
    maven {
        groupId = "cn.shper.build"
        artifactId = "zz-plugin"
        version = "1.0.0"

        repository {
            url = "https://maven.shper.cn/release"
            userName = "XXX"
            password = "XXX"

            sourcesJar = false
            javadocJar = true
        }

        snapshotRepository {
            url = "https://maven.shper.cn/snapshots"
            userName = "XXX"
            password = "XXX"

            sourcesJar = true
            javadocJar = true
        }

        bintray {
            repo = "maven"
            userOrg = "XXX"
            name = "ZZ-Plugins"
            desc = 'Shper ZZ Plugins for Gradle'
            websiteUrl = "https://www.shper.cn"
            vcsUrl = "https://www.shper.cn"
        }

    }
}
```


## License
Shper ZZ-Plugins is MIT licensed, as found in the LICENSE file.

Shper ZZ-Plugins documentation is Creative Commons licensed, as found in the LICENSE-docs file.