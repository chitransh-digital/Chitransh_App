plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.example.communityapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.communityapp"
        minSdk = 24
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-firestore:24.10.0")
    implementation("com.google.firebase:firebase-auth:22.3.0")
    implementation("com.google.firebase:firebase-storage:20.3.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.5.3")
    implementation("androidx.navigation:navigation-ui-ktx:2.5.3")
    implementation("androidx.annotation:annotation:1.7.0")
    implementation("com.google.firebase:firebase-messaging:23.4.0")
    implementation("com.google.firebase:firebase-analytics:21.5.0")
    implementation("androidx.activity:activity:1.8.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    //hilt
    implementation("com.google.dagger:hilt-android:2.44")
    kapt("com.google.dagger:hilt-android-compiler:2.44")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")

    val lifecycle_version = "2.5.0"
    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version")
    // LiveData
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version")
    // Saved state module for ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-savedstate:$lifecycle_version")
    // Annotation processor
    kapt("androidx.lifecycle:lifecycle-compiler:$lifecycle_version")

    //DataStore
    implementation( "androidx.datastore:datastore-preferences:1.0.0")

    implementation("com.squareup.okhttp3:logging-interceptor:3.14.2")
    implementation("androidx.datastore:datastore-preferences-core:1.0.0")

    //Room
    implementation("androidx.room:room-ktx:2.5.2")
    kapt("androidx.room:room-compiler:2.5.2")

    implementation ("androidx.browser:browser:1.3.0")
    implementation ("com.google.firebase:firebase-appcheck-safetynet:16.0.0-beta04")


    //viewPage2
    implementation ("androidx.viewpager2:viewpager2:1.1.0-alpha01")

    //glide
    implementation ("com.github.bumptech.glide:glide:4.16.0")

    //Carousel
    implementation ("me.relex:circleindicator:2.1.6")

    implementation ("org.imaginativeworld.whynotimagecarousel:whynotimagecarousel:2.1.0")

}