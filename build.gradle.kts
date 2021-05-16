import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
val invoker: Configuration by configurations.creating

plugins {
    kotlin("jvm") version "1.5.0"
    id("com.github.johnrengelman.shadow") version "7.0.0"
    application
}
group = "dev.manka.kotlin.function"
version = "1.0"

application {
    mainClass.set("$group.AppKt")
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test-junit"))
    implementation("org.slf4j:slf4j-simple:1.7.30")
    implementation("io.github.microutils:kotlin-logging-jvm:2.0.6")
    implementation("com.google.cloud.functions:functions-framework-api:1.0.4")
    implementation("com.google.cloud:google-cloud-firestore:2.3.0")
    invoker("com.google.cloud.functions.invoker:java-function-invoker:1.0.2")
}

tasks.test {
    useJUnit()
}

tasks.build {
    dependsOn(":shadowJar")
}

buildscript {

}
tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

task<JavaExec>("runFunction") {
    main = "com.google.cloud.functions.invoker.runner.Invoker"
    classpath(invoker)
    inputs.files(configurations.runtimeClasspath, sourceSets["main"].output)
    args(
        "--target", project.findProperty("runFunction.target") ?:  "${project.group}.App",
        "--port", project.findProperty("runFunction.port") ?: 8080
    )
    doFirst {
        args("--classpath", files(configurations.runtimeClasspath, sourceSets["main"].output).asPath)
    }
}

task("buildFunction") {
    dependsOn("build")
    delete("build/deploy")
    copy {
        from("build/libs/${project.name}-$version-all.jar")
        into("build/deploy")
    }
}