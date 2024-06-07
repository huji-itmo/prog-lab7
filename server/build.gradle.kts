plugins {
    idea
    id("java")
    id("java-library")
    id("io.freefair.lombok") version "8.6"
}

group = "org.example"
repositories {
    mavenCentral()
}

dependencies {
    implementation("org.hibernate:hibernate-gradle-plugin:5.6.3.Final")

    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")

    implementation("org.postgresql:postgresql:42.2.8")

    implementation(fileTree(mapOf("dir" to "../shared/build/libs", "include" to listOf("*.jar"))))

    testCompileOnly("org.projectlombok:lombok:1.18.30")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.30")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "Main"
    }
    val dependencies = configurations
            .runtimeClasspath
            .get()
            .map(::zipTree) // OR .map { zipTree(it) }
    from(dependencies)
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.withType<JavaCompile> {
    options.compilerArgs.addAll(arrayOf("--release", "17"))
}

tasks.compileJava{
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

tasks.create("deploy") {

    dependsOn("jar")

    doLast {
        val user = (System.getenv("DEPLOYUSER") ?: "ERROR")
        val userAndHost : String = user + "@" + (System.getenv ("DEPLOYHOST")  ?: "ERROR")

        val pwd : String = System.getenv("DEPLOYPWD") ?: "ERROR"

        println("$userAndHost :$pwd")

        exec {
            workingDir(".")
            commandLine("pscp", "-pw", pwd, "-P", 2222, "${project.rootDir}/build/libs/**.jar", "$userAndHost:/home/studs/$user/prog/lab7/")
//            commandLine("plink", "-x", "-a", "-ssh", userAndHost, "-P", "2222", "-pw", pwd)
        }
    }
}