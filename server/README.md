# HealthSSI PIS Server

## Introduction

This is a ktor project that runs a Practice Information System (PIS) that
manages patients, medications and prescriptions. The server exposed its 
functionality via a REST API.

## Features

### Medical prescriptions secured with self-sovereign identity technology

HealthSSI PIS Server currently implements the backend features described in https://github.com/Abdagon/health-ssi-2

## Technology

The following additional libraries are used
- Koin for dependency injection
- Exposed as the ORM
- H2 DB for DEV
- Postgresql for TEST/PROD


## How to run

Create a run configuration to execute ch.healthwallet.web.ApplicationKt.

By default this will load the application.yaml under src/main/resources.

You can specify different config files using "Program arguments", 
e.g. for test use -config=application-test.yaml.

Make sure to provide any environment variables required by the config file 
(e.g. $DB_USER/$DB_PASSWORD) in the run configuration.

