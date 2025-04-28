plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.todolist"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.todolist"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
}


dependencies {

    // AppCompat, Material Design, ConstraintLayout
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)

    // Lifecycle
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)

    // Navigation
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)

    // Activity
    implementation(libs.activity)

    // Firebase
    implementation(libs.firebase.database)
    implementation ("com.google.firebase:firebase-storage:21.0.1")
    implementation (libs.google.firebase.database.v2003)
    implementation ("com.google.firebase:firebase-auth:21.0.0")
    implementation ("com.google.firebase:firebase-appcheck:18.0.0")

    implementation ("com.github.bumptech.glide:glide:4.15.1")  // Use the latest stable version
    annotationProcessor ("com.github.bumptech.glide:compiler:4.15.1" ) // Required for annotation processing
    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Networking
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")

    // Image Loading
//    implementation("com.github.bumptech.glide:glide:4.15.1")
//    annotationProcessor("com.github.bumptech.glide:compiler:4.15.1")
//    implementation("com.squareup.picasso:picasso:2.71828")

    //hashing
    implementation ("org.mindrot:jbcrypt:0.4")

    // RecyclerView
    implementation("androidx.recyclerview:recyclerview:1.3.2")

    // Volley
    implementation(libs.volley)

    // CoordinatorLayout
    implementation("androidx.coordinatorlayout:coordinatorlayout:1.2.0")
}