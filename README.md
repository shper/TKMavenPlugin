![TK-Maven](media/guide.png)

-------

## New version


## Prerequisites

The version of the Gradle requires to match TK-Maven's version.

### TK-Maven version description

| TK-Maven | Support <br /> Gradle version | Support <br /> Android build tools |
|:--------:|:---:|:---:|
|  2.0.0+  | >= 6.0.0 | >= 4.0.0 |
|  1.0.0+  | <= 5.6.4 | <= 3.6.3 |


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


## License

-------

    Copyright 2022 Shper
    
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    
       http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
