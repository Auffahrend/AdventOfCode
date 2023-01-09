import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val jvmVersion = "11"
val kotlinVersion = "1.7.22"

plugins {
    kotlin("jvm") version "1.7.22"
    application
}

group = "me.akkosten"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")
    testImplementation(kotlin("test"))
//    implementation(kotlin("stdlib-jdk11"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = jvmVersion
}

tasks.withType<Test> {
//    minHeapSize = "512m"
    maxHeapSize = "8g"
//    jvmArgs = listOf("-XX:MaxPermSize=512m")
}

application {
    mainClass.set("MainKt")
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = jvmVersion
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = jvmVersion
}