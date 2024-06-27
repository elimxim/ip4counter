plugins {
    java
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.junit.jupiter:junit-jupiter-params")
}

tasks.test {
    minHeapSize = "2g"
    maxHeapSize = "4g"
    useJUnitPlatform()
    testLogging {
        showExceptions = true
        showCauses = true
        showStackTraces = true
        showStandardStreams = true
        events("failed")
    }
}