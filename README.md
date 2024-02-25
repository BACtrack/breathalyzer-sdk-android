[![GitHub](https://img.shields.io/github/license/BACtrack/breathalyzer-sdk-android)](https://github.com/BACtrack/breathalyzer-sdk-android/blob/main/LICENSE.md)
[![Latest](https://jitpack.io/v/BACtrack/breathalyzer-sdk.svg)](https://jitpack.io/#BACtrack/breathalyzer-sdk)

## BACtrack Android Breathalyzer SDK

The purpose of this SDK is to establish a connection with the BACtrack Breathalyzer products' SDK
and enable users to take a test and obtain BAC results. Additionally, users can retrieve general
information about the Breathalyzer, such as the serial number and battery.

For SDK documentation, visit https://developer.bactrack.com/


Table of contents
=================

* [Installation](#installation)
* [Run Demo](#run-demo)

## Installation

1. Include the [JitPack](https://jitpack.io/#BACtrack/breathalyzer-sdk) repository in your project's
   build file
   <details>
      <summary>Kotlin DSL</summary>

      ```kotlin
      // settings.gradle.kts
      dependencyResolutionManagement {
          repositories {
              maven(url = "https://jitpack.io")
          }
      }
      ```
   </details>

   <details open>
      <summary>Groovy</summary>

      ```groovy
      // settings.gradle
      dependencyResolutionManagement {
          repositories {
              maven { url "https://jitpack.io" }
          }
      }
      ```
   </details>

2. Add the dependencies to your app's build file
   <details>
      <summary>Kotlin DSL</summary>

      ```kotlin
      // build.gradle.kts
      dependencies {
          implementation("com.github.BACtrack:breathalyzer-sdk:${bactrack_sdk_version}")
      }
      ```
     </details>

   <details open>
      <summary>Groovy</summary>
   
      ```groovy
       // build.gradle
       dependencies {
           implementation "com.github.BACtrack:breathalyzer-sdk:${bactrack_sdk_version}"
       }
      ```
   </details>

## Run Demo

Clone this repository and make sure you're using the latest version available
In order to start testing, you need to add your API key to `MainActivity.java`.
After that, you can follow the next steps:

1) Run the app
2) Turn on your breathalyzer.
3) Tap on `Connect Breathalyzer` to establish a connection.
4) You can now fetch information such as the serial number or battery status.
5) Additionally, you can also tap on `Start Test Countdown` to start taking a test


See video for usage reference below:



https://github.com/BACtrack/breathalyzer-sdk-android/assets/47486976/779d2c11-8ab9-441f-8678-992104722953



