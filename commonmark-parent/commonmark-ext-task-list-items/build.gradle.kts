/*
 * This file was generated by the Gradle 'init' task.
 */

plugins {
    id("buildlogic.java-conventions")
}

dependencies {
    api(project(":commonmark"))
    testImplementation(project(":commonmark-test-util"))
}

description = "commonmark-java extension for task list items"
