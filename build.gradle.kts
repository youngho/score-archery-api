buildscript {
	dependencies {
		classpath("org.flywaydb:flyway-mysql:11.18.0")
	}
}

plugins {
	java
	war
	id("org.springframework.boot") version "4.0.1"
	id("io.spring.dependency-management") version "1.1.7"
	id("org.flywaydb.flyway") version "11.18.0"
}

group = "to.yho"
version = "0.0.1-SNAPSHOT"
description = "API for Score Archery"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")
	providedRuntime("org.springframework.boot:spring-boot-starter-tomcat-runtime")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	// https://mvnrepository.com/artifact/org.flywaydb/flyway-core
	implementation("org.flywaydb:flyway-core")
	implementation("org.flywaydb:flyway-mysql")
	// https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-log4j2
//    implementation("org.springframework.boot:spring-boot-starter-log4j2:4.0.0")
	// https://mvnrepository.com/artifact/org.mariadb.jdbc/mariadb-java-client
	implementation("org.mariadb.jdbc:mariadb-java-client:3.5.6")
// https://mvnrepository.com/artifact/org.springdoc/springdoc-openapi-starter-webmvc-ui
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.14")
}

flyway {
	url = "jdbc:mariadb://146.56.105.224:3306/SCORE_ARCHERY"
	user = "sarchery"
	password = "20260112As!"
	baselineVersion = "0"
	cleanDisabled = false
}


tasks.withType<Test> {
	useJUnitPlatform()
}
