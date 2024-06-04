plugins {
    idea
    id("java")
    id("java-library")
    id("io.freefair.lombok") version "8.6"
}
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.hibernate:hibernate-gradle-plugin:5.6.3.Final")
    implementation("org.postgresql:postgresql:42.2.8")

    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")
}

tasks.test {
    useJUnitPlatform()
}