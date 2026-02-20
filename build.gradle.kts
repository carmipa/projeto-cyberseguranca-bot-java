plugins {
    java
    id("org.springframework.boot") version "4.0.3"
    id("io.spring.dependency-management") version "1.1.7"
    id("com.google.cloud.tools.jib") version "3.4.4"
}

group = "br.com.bot"
version = "0.0.1-SNAPSHOT"
description = "projeto-cyberseguranca-bot-java"

java {
    toolchain {
       languageVersion.set(JavaLanguageVersion.of(25))
    }
}

repositories {
    mavenCentral()
}

dependencies {

    implementation("com.rometools:rome:2.1.0")
    // Spring Boot Core
    implementation("org.springframework.boot:spring-boot-starter")

    // Habilita o RestTemplate e serviços Web (CORRIGIDO PARA KOTLIN DSL)
    implementation("org.springframework.boot:spring-boot-starter-web")

    // Desenvolvimento e Docker
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    developmentOnly("org.springframework.boot:spring-boot-docker-compose")

    // --- DEPENDÊNCIAS DO BOT ---
    // JDA: Biblioteca do Discord
    implementation("net.dv8tion:JDA:5.3.0")

    // Jackson: Suporte para LocalDateTime no JSON (Essencial para o seu Record)
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")

    // Validação de dados (Importante para GRC e Cyber)
    implementation("org.springframework.boot:spring-boot-starter-validation")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

// Configuração do Jib para o seu Docker
jib {
    to {
        image = "cyberbot-carminati"
    }
    container {
        // AJUSTADO: Nome da sua classe principal de acordo com o log anterior
        mainClass = "br.com.bot.cyberseguranca.ProjetoCybersegurancaBotJavaApplication"
        jvmFlags = listOf("-XX:+UseZGC", "-XX:+ZGenerational")
    }
}