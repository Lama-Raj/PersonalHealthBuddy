import org.gradle.internal.impldep.com.amazonaws.auth.policy.Principal
import org.gradle.kotlin.dsl.implementation
import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")
    id ("kotlin-parcelize")
}

android {
    namespace = "com.unh.personal_health_buddy"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.unh.personal_health_buddy"
        minSdk = 33
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Read GOOGLE_CLIENT_ID from gradle.properties
        val googleClientId: String = project.findProperty("GOOGLE_CLIENT_ID") as? String
            ?: throw GradleException("GOOGLE_CLIENT_ID is missing! Add it to gradle.properties file.")

        buildConfigField("String", "GOOGLE_CLIENT_ID", "\"$googleClientId\"")

        // Read NEWS_API_KEY from local.properties to keep it safe
        val localProperties = Properties()
        val localPropertiesFile = project.rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            localProperties.load(FileInputStream(localPropertiesFile))
        }
        val newsApiKey = localProperties.getProperty("NEWS_API_KEY") ?: ""

        buildConfigField("String", "NEWS_API_KEY", "\"$newsApiKey\"")

    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
//
//    packagingOptions {
//        resources.excludes.add("META-INF/DEPENDENCIES")
//    }
    packaging {
        resources {
            excludes += setOf(
                "META-INF/LICENSE.md",
                "META-INF/LICENSE.txt",
                "META-INF/NOTICE.md",
                "META-INF/NOTICE.txt"
            )
        }
    }
}



configurations.all {
    resolutionStrategy {
        force("androidx.test.espresso:espresso-core:3.5.1")
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Compose BOM
    implementation(platform(libs.androidx.compose.bom))

    // Compose libraries
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.animation:animation-core")
    implementation(libs.androidx.animation.core.lint)

    implementation("androidx.compose.material:material-icons-extended:1.5.0")
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.androidx.navigation.common.ktx)
    implementation(libs.ads.mobile.sdk)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.runtime)
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.play.services.nearby)
    implementation(libs.androidx.foundation)
    implementation(libs.identity.jvm)
    implementation(libs.car.ui.lib)
    implementation(libs.androidx.benchmark.traceprocessor)
    implementation(libs.androidx.camera.camera2.pipe)
    implementation(libs.androidx.foundation.layout)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.text)
    implementation(libs.compose.material3)
    implementation(libs.ui.text)
    implementation(libs.foundation.layout)
    implementation(libs.androidx.tools.core)


    // Optional: debugging / tooling
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // Navigation
    val nav_version = "2.9.5"
    implementation("androidx.navigation:navigation-compose:$nav_version")

//    // Firebase
//    implementation(platform("com.google.firebase:firebase-bom:34.4.0"))
//    implementation("com.google.firebase:firebase-analytics:23.0.0")
//    implementation("com.google.firebase:firebase-auth-ktx:23.2.1")

    // 🔥 Firebase (stable BoM)
    implementation(platform("com.google.firebase:firebase-bom:33.3.0"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-analytics-ktx")

    // Principal.WebIdentityProviders.Google Sign-In (stable version)
    implementation("com.google.android.gms:play-services-auth:21.3.0")

  //Optional but useful
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")
    implementation("com.google.android.gms:play-services-auth:21.4.0")


    // Play Services / Maps
//    implementation("com.google.android.gms:play-services-auth:21.4.0")
//    implementation("com.google.android.gms:play-services-maps:19.2.0")
    implementation("com.google.maps.android:maps-compose:4.3.0")

    // Coroutines for Play Services
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.6.4")

    // WebView
    implementation("androidx.webkit:webkit:1.8.0")

    // Unit testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation("io.getstream:stream-chat-android-ui-components:6.26.0")
    implementation("com.google.firebase:firebase-storage-ktx:21.0.1")
    implementation("io.coil-kt:coil-compose:2.4.0")



    implementation("com.sun.mail:android-mail:1.6.7")
    implementation("com.sun.mail:android-activation:1.6.7")

    implementation("androidx.compose.ui:ui-test-junit4")
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.36.0")

    implementation("androidx.compose.material3:material3:1.2.0")


    implementation(libs.androidx.work.ktx)

}
