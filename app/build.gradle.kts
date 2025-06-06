plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("momoi.plugin.apkmixin") apply true
}

android {
    namespace = "momoi.mod.qqpro"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.tencent.qqlite"
        minSdk = 21
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    compileOnly(libs.androidx.fragment)
    implementation(project(":ApkMixin-annotation"))
    compileOnly(libs.androidx.constraintlayout)
    compileOnly(libs.androidx.recyclerview)
}

apkMixin {
    versionName = "1.2"
    targetApk = "nwearqq2.apk"
    useProcessorCountAsThreadCount = project.properties["useProcessorCountAsThreadCount"] == "true"

    signing {
        keyFile = file("dist/testkey.pk8")
        certFile = file("dist/testkey.x509.pem")
    }

    output {
        signedFileName = "QQPro_${versionName}.apk"
    }
}