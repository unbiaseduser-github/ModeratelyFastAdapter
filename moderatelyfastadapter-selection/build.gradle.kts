plugins {
    alias(libs.plugins.android.library)
    id("maven-publish")
}

android {
    namespace = "com.unbiaseduser.moderatelyfastadapter.selection"
    compileSdk {
        version = release(37)
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
    }

    defaultConfig {
        minSdk = 23

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

}

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "com.unbiaseduser"
            artifactId = "moderatelyfastadapter-selection"
            version = "0.1.0"

            afterEvaluate {
                from(components["release"])
            }

            pom {
                name.set("moderatelyfastadapter-selection")
                description.set("JavaFX Cell-inspired RecyclerView Adapters")
                url.set("https://github.com/unbiaseduser-github/ModeratelyFastAdapter")

                licenses {
                    license {
                        name.set("Apache-2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                        distribution.set("repo")
                    }
                }

                developers {
                    developer {
                        id.set("unbiaseduser-github")
                        name.set("Dang Quang Trung")
                        email.set("quangtrung02hn16@gmail.com")
                        url.set("https://github.com/unbiaseduser-github")
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com/unbiaseduser-github/ModeratelyFastAdapter.git")
                    developerConnection.set("scm:git:ssh://github.com:unbiaseduser-github/ModeratelyFastAdapter.git")
                    url.set("https://github.com/unbiaseduser-github/ModeratelyFastAdapter/tree/master")
                }
            }
        }
    }
}


dependencies {
    api(project(":moderatelyfastadapter"))
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    api(libs.androidx.recyclerview.selection)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
}