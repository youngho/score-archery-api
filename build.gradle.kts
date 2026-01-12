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
	implementation("org.springframework.boot:spring-boot-starter-flyway")
	implementation("org.springframework.boot:spring-boot-starter-webmvc")

	compileOnly("org.projectlombok:lombok")
	runtimeOnly("org.mariadb.jdbc:mariadb-java-client")
	annotationProcessor("org.projectlombok:lombok")
	providedRuntime("org.springframework.boot:spring-boot-starter-tomcat-runtime")
	testImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test")
	// https://mvnrepository.com/artifact/org.flywaydb/flyway-core
	implementation("org.flywaydb:flyway-core")
	implementation("org.flywaydb:flyway-mysql")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

flyway {
	url = "jdbc:mariadb://146.56.105.224:3306/SCORE_ARCHERY"
	user = "sarchery"
	password = "20260112As!!"
	baselineVersion = "0"
}


tasks.withType<Test> {
	useJUnitPlatform()
}
