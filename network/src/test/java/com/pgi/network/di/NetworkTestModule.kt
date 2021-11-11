package com.pgi.network.di

import com.pgi.network.GMWebServiceManager
import org.koin.dsl.module

val networkTestModule = module {
    single { GMWebServiceManager() }
}