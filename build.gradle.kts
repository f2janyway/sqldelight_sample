// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.1.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
    id("app.cash.sqldelight") version "2.0.0-rc02"
    //this is hack-key for setting sqldelight build.gradle
    //for this error:
    // - SQL Delight Gradle plugin applied in project ':' but no supported Kotlin plugin was found
    kotlin("jvm") version "1.9.0"
}

sqldelight {
    databases {
        create("Database") {
            //this package name is must same with
            // src/main/sqldelight
            //  - com/example/sql_test
            // when create directory in IDE write like this "com/example/sql_text/"
            // do not "com.example.sql_text" in window, it may different with mac
            // when creating directory
            // or create in folder or finder
            packageName.set("com.example.sql_test")
        }
    }
}