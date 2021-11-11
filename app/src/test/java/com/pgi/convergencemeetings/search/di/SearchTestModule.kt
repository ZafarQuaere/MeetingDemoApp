package com.pgi.convergencemeetings.search.di

import com.pgi.convergencemeetings.greendao.ApplicationDao
import com.pgi.network.models.SearchResult
import com.pgi.network.repository.GMElasticSearchRepository
import com.pgi.convergencemeetings.search.ui.JoinFragmentViewModel
import com.pgi.convergencemeetings.utils.AppAuthUtils
import io.mockk.every
import io.mockk.mockkClass
import io.reactivex.Observable
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val searchGuestTestModule = module {
  val mockAppAuthUtils = mockkClass(AppAuthUtils::class, relaxed = true)
  val mockApplicationDao: ApplicationDao = mockkClass(ApplicationDao::class, relaxed = true)
  val mockGMElasticSearchRepository: GMElasticSearchRepository =
    mockkClass(GMElasticSearchRepository::class, relaxed = true)

  val mockSeacrhResult = SearchResult(
			123213213L,
			313123,
			31321321,
			"312312312",
			"Test",
			31231,
			false,
			312312,
			"globalmeet5",
			"https://pgi.globalmeet.com/test",
			"121233",
			"3423423423423",
			"1233",
			"HOST",
			"Test",
			"User",
			"https://pgi.globalmeet.com/test",
			12123,
			true,
			0,
			"https://pgi.globalmeet.com/sampleimage"
																																							 )
  every {
    mockAppAuthUtils.isUserTypeGuest
  } returns true

  every {
    mockApplicationDao.allRecentMeetings
  } returns listOf(mockSeacrhResult)

  every {
    mockGMElasticSearchRepository.suggest(any(), any())
  } returns Observable.just(listOf(mockSeacrhResult))

  every {
    mockGMElasticSearchRepository.search(any(), any(), any())
  } returns Observable.just(listOf(mockSeacrhResult))

  every {
    mockGMElasticSearchRepository.getMeetingRoomInfoFromFurl(any(), any())
  } returns Observable.just(mockSeacrhResult)

  viewModel {
    JoinFragmentViewModel(mockAppAuthUtils, mockApplicationDao, mockGMElasticSearchRepository)
  }
}

val searchTestModule = module {
  val mockAppAuthUtils = mockkClass(AppAuthUtils::class, relaxed = true)
  val mockApplicationDao: ApplicationDao = mockkClass(ApplicationDao::class, relaxed = true)
  val mockGMElasticSearchRepository: GMElasticSearchRepository =
    mockkClass(GMElasticSearchRepository::class, relaxed = true)

  val mockSeacrhResult = SearchResult(
			123213213L,
			313123,
			31321321,
			"312312312",
			"Test",
			31231,
			false,
			312312,
			"globalmeet5",
			"https://pgi.globalmeet.com/test",
			"121233",
			"3423423423423",
			"1233",
			"HOST",
			"Test",
			"User",
			"https://pgi.globalmeet.com/test",
			12123,
			true,
			0,
			"https://pgi.globalmeet.com/sampleimage"
																																							 )
  every {
    mockAppAuthUtils.isUserTypeGuest
  } returns false

  every {
    mockApplicationDao.allRecentMeetings
  } returns listOf(mockSeacrhResult)

  every {
    mockGMElasticSearchRepository.suggest(any(), any())
  } returns Observable.just(listOf(mockSeacrhResult))

  every {
    mockGMElasticSearchRepository.search(any(), any(), any())
  } returns Observable.just(listOf(mockSeacrhResult))

  every {
    mockGMElasticSearchRepository.getMeetingRoomInfoFromFurl(any(), any())
  } returns Observable.just(mockSeacrhResult)

  viewModel {
    JoinFragmentViewModel(mockAppAuthUtils, mockApplicationDao, mockGMElasticSearchRepository)
  }
}