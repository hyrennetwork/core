plugins {
	kotlin("jvm") version "1.5.20"
	kotlin("plugin.serialization") version "1.5.20"

	id("com.github.johnrengelman.shadow") version "7.0.0"

	`maven-publish`
}

allprojects {
	plugins.apply("org.jetbrains.kotlin.plugin.serialization")
	plugins.apply("com.github.johnrengelman.shadow")
	plugins.apply("org.jetbrains.kotlin.jvm")
	plugins.apply("maven-publish")

	tasks {
		compileKotlin {
			kotlinOptions {
				jvmTarget = "1.8"
			}
		}

		shadowJar {
			archiveFileName.set("${project.name}.jar")
		}
	}

	group = "net.hyren"
	version = "0.1-ALPHA"

	repositories {
		mavenCentral()

		maven("https://repository.hyren.net/") {
			credentials {
				username = System.getenv("MAVEN_USERNAME")
				password = System.getenv("MAVEN_PASSWORD")
			}
		}
	}

	configurations.all {
		resolutionStrategy.cacheChangingModulesFor(120, "seconds")
	}

	val sources by tasks.registering(Jar::class) {
		archiveFileName.set(project.name)
		archiveClassifier.set("sources")
		archiveVersion.set(null as String?)

		from(sourceSets.main.get().allSource)
	}

	if (project.name != "core") {
		publishing {
			publications {
				create<MavenPublication>("maven") {
					repositories {
						maven("https://repository.hyren.net/") {
							credentials {
								username = System.getenv("MAVEN_USERNAME")
								password = System.getenv("MAVEN_PASSWORD")
							}
						}
					}

					from(components["kotlin"])
					artifact(sources.get())
				}
			}
		}
	}
}
