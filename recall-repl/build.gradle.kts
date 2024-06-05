version = "0.0.1-SNAPSHOT"

dependencyManagement {
    imports {
        mavenBom ("org.springframework.shell:spring-shell-dependencies:3.2.5")
    }
}

dependencies {
    implementation("org.springframework.shell:spring-shell-starter")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    implementation(project(":recall-server")) {
        exclude("org.springframework.boot", "spring-boot-starter-data-jpa")
        exclude("org.springframework.boot", "spring-boot-starter-web")
        exclude("org.postgresql", "postgresql")
    }
}