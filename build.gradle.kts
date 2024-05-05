import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project

plugins {
    kotlin("jvm") version "1.9.23"
    id("io.ktor.plugin") version "2.3.10"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.23"
}

group = "io.github.cong"
version = "1.0.0"

kotlin {
    jvmToolchain(8)
}
tasks {
    withType<KotlinCompile>().configureEach {
        kotlinOptions.jvmTarget = "1.8"
    }
}

application {
    mainClass.set("io.github.cong.multiplayer.MultiplayerKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    val inChina = System.getProperty("user.timezone") in arrayOf("Asia/Shanghai", "GMT+08:00")
    mavenLocal()
    if (inChina)
        maven(url = "https://maven.aliyun.com/repository/public")//mirror for central
    mavenCentral()
    maven(url = "https://raw.githubusercontent.com/Zelaux/MindustryRepo/master/repository")
    maven(url = "https://www.jitpack.io")
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-content-negotiation-jvm")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm")
    implementation("io.ktor:ktor-server-cio-jvm")
    implementation("io.ktor:ktor-server-websockets-jvm")
    implementation("ch.qos.logback:logback-classic:$logback_version")

    implementation("com.github.Anuken.Mindustry:core:v146")
    implementation("com.github.Anuken.Arc:arc-core:v146")
}