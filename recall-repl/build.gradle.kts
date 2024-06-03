version = "0.0.1-SNAPSHOT"

dependencyManagement {
    imports {
        mavenBom ("org.springframework.shell:spring-shell-dependencies:3.2.5")
    }
}

dependencies {
    implementation("org.springframework.shell:spring-shell-starter")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.postgresql:postgresql")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}