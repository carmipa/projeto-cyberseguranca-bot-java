plugins {
    java
    id("org.springframework.boot") version "4.0.3"
    id("io.spring.dependency-management") version "1.1.7"
    // Adicionando o Jib para facilitar o build da imagem Docker no seu desktop
    id("com.google.cloud.tools.jib") version "3.4.4"
}

group = "br.com.bot"
version = "0.0.1-SNAPSHOT"
description = "projeto-cyberseguranca-bot-java"

java {
    toolchain {
       // Perfeito! Java 25 extraindo o máximo do seu hardware
       languageVersion = JavaLanguageVersion.of(25)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Spring Boot Core
    implementation("org.springframework.boot:spring-boot-starter")

    // Suas ferramentas de desenvolvimento (Docker Compose no Spring é top!)
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    developmentOnly("org.springframework.boot:spring-boot-docker-compose")

    // --- DEPENDÊNCIAS DO BOT (Adicionadas) ---
    // JDA: Biblioteca do Discord
    implementation("net.dv8tion:JDA:5.3.0")

    // Jackson: Para persistência em JSON (sem DTO, direto na Model/Record)
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")

    // Validação de dados (Importante para GRC e Cyber)
    implementation("org.springframework.boot:spring-boot-starter-validation")
    // ------------------------------------------

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
        mainClass = "br.com.bot.CyberBotApplication" // Ajuste para sua classe Main real
        jvmFlags = listOf("-XX:+UseZGC", "-XX:+ZGenerational")
    }
}