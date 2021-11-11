package com.pgi.convergencemeetings.base.di

import com.pgi.convergence.agenda.ui.MeetingsAgendaViewModel
import com.pgi.convergence.application.CoreApplication
import com.pgi.convergence.common.profile.ProfileManager
import com.pgi.convergence.common.features.FeaturesConfigService
import com.pgi.convergence.common.features.FeaturesManager
import com.pgi.convergence.data.repository.msal.MSALAuthRespositoryImpl
import com.pgi.convergence.data.repository.msal.MSALGraphRepositoryImpl
import com.pgi.convergence.home.ui.HomeCardsViewModel
import com.pgi.convergence.persistance.SharedPreferencesManager
import com.pgi.convergence.ui.SharedViewModel
import com.pgi.convergencemeetings.BuildConfig
import com.pgi.convergencemeetings.home.ui.AppBaseViewModel
import com.pgi.convergencemeetings.greendao.ApplicationDao
import com.pgi.convergencemeetings.search.ui.JoinFragmentViewModel
import com.pgi.convergencemeetings.home.services.LogoutUserService
import com.pgi.convergencemeetings.utils.AppAuthUtils
import com.pgi.convergencemeetings.utils.ClientInfoDaoUtils
import com.pgi.network.GMWebServiceManager
import com.pgi.network.TurnServerInfoManager
import com.pgi.network.repository.GMElasticSearchRepository
import com.pgi.network.repository.GMWebServiceRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.serialization.UnstableDefault
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

@UseExperimental(UnstableDefault::class)
@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
val baseModule = module {

	single { MSALGraphRepositoryImpl() }

	single { LogoutUserService() }

	single { SharedViewModel(androidContext()) }

	factory { ProfileManager(GMElasticSearchRepository.INSTANCE, CoreApplication.mLogger) }

	single { FeaturesConfigService() }

	single { FeaturesManager(get(), CoreApplication.mLogger) }

	factory { MSALAuthRespositoryImpl(if(BuildConfig.FLAVOR.contains("lumen")) { "lumen"} else { "pgi"}) }

	single { GMWebServiceRepository() }

	single { GMWebServiceManager() }

	// TODO:: Using the singleton instance here. We need to inject this form application context
	// have to wait until we can add DI for logger across the app
	viewModel {
		HomeCardsViewModel(
				get(),
				get(),
				get(),
				SharedPreferencesManager.getInstance(),
				get(),
				CoreApplication.mLogger)
	}

	viewModel {
		AppBaseViewModel(
				AppAuthUtils.getInstance(),
				ClientInfoDaoUtils.getInstance(),
				get(),
				get(),
				get(),
				CoreApplication.mLogger,
				SharedPreferencesManager.getInstance())
	}


	viewModel {
		MeetingsAgendaViewModel(
				get(),
				get(),
				get(),
				SharedPreferencesManager.getInstance(),
				get(),
				CoreApplication.mLogger,
				get())
	}


	viewModel {
		JoinFragmentViewModel(
				AppAuthUtils.getInstance(),
				ApplicationDao.get(CoreApplication.appContext),
				GMElasticSearchRepository.INSTANCE)
	}

	single { TurnServerInfoManager()}
}