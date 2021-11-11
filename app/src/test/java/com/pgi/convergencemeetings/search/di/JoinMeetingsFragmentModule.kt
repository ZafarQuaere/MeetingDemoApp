package com.pgi.convergencemeetings.search.di

import com.pgi.convergencemeetings.greendao.ApplicationDao
import com.pgi.convergencemeetings.search.ui.JoinFragmentViewModel
import com.pgi.convergencemeetings.utils.AppAuthUtils
import com.pgi.network.repository.GMElasticSearchRepository
import io.mockk.mockkClass
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val joinMeetingFragmentModule = module {
    val appAuthMock = mockkClass(AppAuthUtils::class, relaxed = true)
    val appDao = mockkClass(ApplicationDao::class, relaxed = true)
    val gmElasticSearchRepoMock = mockkClass(GMElasticSearchRepository::class, relaxed = true)

    val model = JoinFragmentViewModel(appAuthMock, appDao, gmElasticSearchRepoMock)
    viewModel { model }
}