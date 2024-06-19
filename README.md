# HealthWallet

## Introduction

HealthWallet is an end user app for self-sovereign health data management.

## Features

### Medical prescriptions secured with self-sovereign identity technology

HealthWallet currently implements the features described in https://github.com/Abdagon/health-ssi-2

## Testing HealthWallet SSI functionality

1. Go to https://web.healthwallet.li and create an account on the hosted wallet
* no need to use a real email and simple password also fine for testing
2. Install mobile app
* For iOS there is a public testing link here: https://testflight.apple.com/join/rysqNrU1
* For Android need to contact cto@deak.ai to be added to testing group
3. Configure mobile app in the settings field
* use default wallet destination (https://wallet.healthwallet.li)
* enter email and password created on hosted wallet
4. Create a patient, prescription and issues a VC using the Swagger UI on https://pis.healthwallet.li
* POST /patients
  * create a patient, the returned patient id is in the Ids array, the Val where Type is 2)
* POST /patients/{id}/medications
  * create a prescription for a patient
  * you can find the GTIN for a medication here: https://compendium.ch/
  * Note the id of the prescription (it's a UUID)
* POST /vc/issue
  * issue a VC for the prescription using the id (UUID) of the prescription)
  * copy openid credential offer string
* POST /utils/qrcode
  * translate the openid credential offer into a QR code
5. Scan the QR code with mobile app and import into wallet
* You can also check the details of the VC in the web wallet
6. Go to the pharmacy app (it's a WASM based web app, it works for sure in Chrome)
* Click on request prescription and scan QR code with mobile app
* Select prescription and see the information flow to the pharmacy

Please provide feedback to feedback@deak.ai




## Technology

### Overview

This is a Kotlin Multiplatform project targeting Android, iOS and Server.

* `/composeApp` is for code that will be shared across your Compose Multiplatform applications.
  It contains several subfolders:
  - `commonMain` is for code that’s common for all targets.
  - Other folders are for Kotlin code that will be compiled for only the platform indicated in the folder name.
    For example, if you want to use Apple’s CoreCrypto for the iOS part of your Kotlin app,
    `iosMain` would be the right folder for such calls.

* `/iosApp` contains iOS applications. Even if you’re sharing your UI with Compose Multiplatform,
  you need this entry point for your iOS app. This is also where you should add SwiftUI code for your project.

* `/server` is for the Ktor server application.

* `/shared` is for the code that will be shared between all targets in the project.
  The most important subfolder is `commonMain`. If preferred, you can add code to the platform-specific folders here too.


Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html),
[Compose Multiplatform](https://github.com/JetBrains/compose-multiplatform/#compose-multiplatform),


### Third-party dependencies

| Library                                                                       | Description                                                                              |
|-------------------------------------------------------------------------------|------------------------------------------------------------------------------------------|
| [walt.id](https://docs.walt.id/)                                              | SSI/VC implementation with API for wallet, verifier and issuer                           |
| [Voyager](https://voyager.adriel.cafe)                                        | Provides Navigation and ViewModel (ScreenModel) for Compose Multiplatform                |
| [Ktor](https://ktor.io/)                                                      | HTTP Server and Client                                                                   |
| [Koin](https://insert-koin.io/)                                               | Pragmatic Kotlin Multiplatform Dependency Injection Framework                            |
| [Ktor Swagger UI](https://github.com/SMILEY4/ktor-swagger-ui)                 | Ktor plugin to document routes, generate an OpenApi Specification and serve a Swagger UI |
| [Kamel Image](https://github.com/Kamel-Media/Kamel) | Asynchronous media loading library for Compose Multiplatform |
| [QRKit](https://github.com/ChainTechNetwork/QRKitComposeMultiplatform) | Kotlin Multiplatform library for Qr-Scan in your Android or iOS App|
