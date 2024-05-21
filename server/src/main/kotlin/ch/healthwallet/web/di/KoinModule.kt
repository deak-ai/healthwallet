package ch.healthwallet.web.di

import ch.healthwallet.db.PisDbRepository
import ch.healthwallet.db.PisDbRepositoryImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val mainModule = module {
    single<PisDbRepository> { PisDbRepositoryImpl() }
}