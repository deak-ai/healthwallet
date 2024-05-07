package ch.healthwallet.di

import org.koin.core.module.Module

internal expect fun getDatastoreModuleByPlatform(): Module

val datastoreModule = getDatastoreModuleByPlatform()