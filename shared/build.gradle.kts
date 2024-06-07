plugins {
    idea
    id("java")
    id("java-library")
    id("io.freefair.lombok") version "8.6"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.hibernate:hibernate-gradle-plugin:5.6.3.Final")
    implementation("org.postgresql:postgresql:42.2.8")

    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")
}

tasks.withType<JavaCompile> {
    options.compilerArgs.addAll(arrayOf("--release", "17"))
}

tasks.compileJava {
    options.encoding = "UTF-8"
}

tasks.javadoc {
    options.encoding = "UTF-8"
}


tasks.register<Jar>("export") {
    manifest {
        attributes["Main-Class"] = "Main"
    }
    archiveBaseName.set(project.name)
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    with(tasks.jar.get())
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    destinationDirectory.set(file("C:/code/itmo/prog/bin"))
}