// build.gradle.kts (Module :app)

plugins {

    id("com.android.application")

    id("org.jetbrains.kotlin.android")

}



android {

    namespace = "com.example.mirandoclock"

    compileSdk = 34



    defaultConfig {

        applicationId = "com.example.mirandoclock"

        minSdk = 21

        targetSdk = 34

        versionCode = 1

        versionName = "1.0"



        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

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

}



dependencies {

    // Core AndroidX libraries

    implementation("androidx.core:core-ktx:1.13.1")

    implementation("androidx.appcompat:appcompat:1.6.1") // Это очень важно для Theme.AppCompat

    implementation("com.google.android.material:material:1.12.0")

    implementation("androidx.constraintlayout:constraintlayout:2.1.4")



    // Activity Result API (для pickImageLauncher)

    implementation("androidx.activity:activity-ktx:1.9.0")



    // Тестовые зависимости

    testImplementation("junit:junit:4.13.2")

    androidTestImplementation("androidx.test.ext:junit:1.1.5")

    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

}