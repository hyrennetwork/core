dependencies {
    // waterfall proxy
    api("io.github.waterfallmc:waterfall-proxy:1.16-R0.5-SNAPSHOT")

    // exposed
    api("org.jetbrains.exposed:exposed-core:0.29.1")
    api("org.jetbrains.exposed:exposed-dao:0.29.1")
    api("org.jetbrains.exposed:exposed-jdbc:0.29.1")
    api("org.jetbrains.exposed:exposed-jodatime:0.29.1")

    // eventbus
    api("org.greenrobot:eventbus:3.2.0")

    // core shared
    implementation(project(":core-shared"))
}