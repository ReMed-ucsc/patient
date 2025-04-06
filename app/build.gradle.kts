import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

val secretsFile = rootProject.file("secret.properties")
val secretsProperties = Properties()
if (secretsFile.exists()) {
    secretsProperties.load(FileInputStream(secretsFile))
} else {
    secretsProperties.setProperty("MAPS_API_KEY", "")
}

android {
    namespace = "com.example.remed"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.remed"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        manifestPlaceholders["MAPS_API_KEY"] = secretsProperties.getProperty("MAPS_API_KEY", "")
        buildConfigField("String", "MAPS_API_KEY", "\"${secretsProperties.getProperty("MAPS_API_KEY", "")}\"")
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.room.common)
    implementation(libs.androidx.activity.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.androidx.work.runtime)

    implementation (libs.retrofit.v2110)
    implementation(libs.squareup.converter.gson)

    implementation(libs.androidx.runtime.livedata)
    implementation(libs.androidx.material)
    implementation(libs.androidx.material.icons.extended)


//    val nav_version = "2.7.7"
    implementation(libs.androidx.navigation.compose)

    implementation(libs.androidx.core.splashscreen)

//    photo picker
    implementation(libs.coil.kt.coil.compose)

//    datastore
    implementation(libs.androidx.datastore.preferences)

//    image loading
    implementation(libs.coil.compose)

    implementation(libs.places)
    implementation(libs.play.services.maps)
    implementation(libs.maps.compose)


}