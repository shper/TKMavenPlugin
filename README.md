![TK-Maven](media/guide.png)

-------

## New version

[![Download](https://api.bintray.com/packages/shper/maven/TK-Maven/images/download.svg)](https://bintray.com/shper/maven/TK-Maven)


## Prerequisites

The version of the Gradle requires to match TK-Maven's version.

### TK-Maven version description

| TK-Maven | Support <br /> Gradle version | Support <br /> Android build tools |
|:---:|:---:|:---:|
| [![Download](https://api.bintray.com/packages/shper/maven/TK-Maven/images/download.svg)](https://bintray.com/shper/maven/TK-Maven) | >= 6.0.0 | >= 4.0.0 |
| [![Download](https://api.bintray.com/packages/shper/maven/TK-Maven/images/download.svg?version=1.0.1)](https://bintray.com/shper/maven/TK-Maven/1.0.1/link) | <= 5.6.4 | <= 3.6.3 |


## Getting Started

-------

### Step 1

Add the TK-Maven to your `buildscript`:

```
buildscript {

    dependencies {
        // TK-Maven 工具
        classpath 'cn.shper.plugin:tk-maven:<last_version>'
    }
}
```

### Step 2

Add the tkmaven extension to your `build.gradle`:

```
apply plugin: 'tk-maven'
tkmaven {
   groupId = "cn.shper.plugin"
   artifactId = "tk-maven"
   version = "1.0.0"

   repository {
        url = "https://maven.shper.cn/repository/release"

        sourcesJar = false
        javadocJar = true
   }

   snapshotRepository {
        url = "https://maven.shper.cn/repository/snapshots"

        sourcesJar = true
        javadocJar = true
    }

    bintray {
        repo = "maven"
        userOrg = "XXX"
        name = "TK-Plugin"
        desc = 'Shper's Plugin for Gradle'
        websiteUrl = "https://www.shper.cn"
        vcsUrl = "https://www.shper.cn"
    }

}
```


## Optional

-------


### User Configuration

#### Configuration by properties file

Add the your username and password to your `local.properties` or `~/.gradle/gradle.properties`:

```
# Maven's username and password
tk-maven.userName=XXX
tk-maven.password=XXX

# Bintray's user and apikey
tk-maven.bintray.user=XXX
tk-maven.bintray.apiKey=XXX
```


#### Configuration by  tkmaven’s extension (Not Recommend)

Add the your username and password to tkmaven:

```
tkmaven {
    // ......
    repository {
       userName = "XXX"
       password = "XXX"
       // ......
    }

    snapshotRepository {
       userName = "XXX"
       password = "XXX"
       
       // ......
    }

    bintray {
       user = "XXX"
       apiKey = "XXX"
       // ......
    }
```


#### Publish

Start to publish you library to repository.

![image-20200611180112334](media/image-20200611180112334.png)

## User  command

#### Publish

Use the command to publish the library

Publish to maven repository.

```
./gradlew publishMaven -PuserName=<&USER_NAME> -Ppassword=<&PASSWORD>
```

Publish to bintray.

```
./gradlew publishBintray -Puser=<&USER> -PapiKey=<&API_KEY>
```



## License

-------

    Copyright 2020 Shper
    
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    
       http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
