import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "3.2.6" apply false
	id("io.spring.dependency-management") version "1.1.5" apply false
	kotlin("jvm") version "1.9.24" apply false
	kotlin("plugin.spring") version "1.9.24" apply false
	kotlin("plugin.jpa") version "1.9.24" apply false
}

group = "cc.green"

subprojects {

	apply(plugin = "java-library")
	apply(plugin = "org.springframework.boot")
	apply(plugin = "io.spring.dependency-management")
	apply(plugin = "org.jetbrains.kotlin.jvm")
	apply(plugin = "org.jetbrains.kotlin.plugin.spring")
	apply(plugin = "org.jetbrains.kotlin.plugin.jpa")

	repositories {
		mavenLocal()
		maven {
			url = uri("https://maven.aliyun.com/repository/central/")
		}
		maven {
			url = uri("https://maven.aliyun.com/repository/gradle-plugin")
		}
		mavenCentral()
	}

	tasks.withType<JavaCompile> {
		sourceCompatibility = JavaVersion.VERSION_17.toString()
	}

	tasks.withType<KotlinCompile> {
		kotlinOptions {
			freeCompilerArgs += "-Xjsr305=strict"
			jvmTarget = "17"
		}
	}

	tasks.withType<Test> {
		useJUnitPlatform()
	}

}