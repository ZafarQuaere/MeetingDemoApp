package com.pgi.convergencemeetings.repository

import com.nhaarman.mockitokotlin2.whenever
import com.pgi.convergence.utils.CommonUtils
import com.pgi.convergencemeetings.RobolectricTest
import com.pgi.network.repository.GMElasticSearchRepository
import com.pgi.network.repository.GMElasticSearchServiceAPI
import com.pgi.network.models.*
import io.reactivex.Observable
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.shouldBeInstanceOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.rule.PowerMockRule

/**
 * Created by Sudheer Chilumula on 9/10/18.
 * PGi
 * sudheer.chilumula@pgi.com
 */

@PrepareForTest(GMElasticSearchServiceAPI::class, GMElasticSearchRepository::class, CommonUtils::class)
class GMElasticSearchRepositoryTest: RobolectricTest() {

    private val suggestResponse = SuggestResponse(id="1026472",
        resourceUri="/meetingrooms/1026472", resourceType="MeetingRoom", title="Kevin McAdams",
        secondaryTitle="http://pgi.globalmeet.com/kevin", externalUrl="http://pgi.globalmeet" +
            ".com/kevin", imageUrl=null, metadata= MetaData(meetingRoomName=null, meetingRoomUrl=null, meetingRoomType="globalmeet5", webMeetingServer="web-na.globalmeet.com", conferenceType="Web and Audio", conferenceId="7638248", ownerEmail=null, ownerGivenName=null, ownerFamilyName=null, brandId=null, images= emptyList()))
    private val mockSuggestResponse = listOf(suggestResponse)
    private val searchResponseItem =  SearchResponseItem(id="1026472",
        resourceUri="/meetingrooms/1026472", resourceType="MeetingRoom", details = Detail
            (title="Kevin McAdams", secondaryTitle="http://pgi.globalmeet.com/kevin",
            externalUrl="http://pgi.globalmeet.com/kevin", imageUrl=null, metadata= MetaData
            (meetingRoomName=null, meetingRoomUrl=null, meetingRoomType="globalmeet5",
            webMeetingServer="web-na.globalmeet.com", conferenceType="Web and Audio", conferenceId="7638248", ownerEmail=null, ownerGivenName=null, ownerFamilyName=null, brandId=null, images= emptyList())))
    private val mockSearchResponse = SearchResponse(totalCount = 1, items = listOf(searchResponseItem))

    @get:Rule
    val rule: PowerMockRule = PowerMockRule()
    @Mock
    private lateinit var mGMElasticSearchServiceAPI: GMElasticSearchServiceAPI
    @Mock
    private lateinit var mCommonUtils: CommonUtils

    @InjectMocks
    private lateinit var mGMElasticSearchRepository: GMElasticSearchRepository

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        PowerMockito.mockStatic(CommonUtils::class.java)
        whenever(CommonUtils.isUsersLocaleJapan()).thenReturn(false)
        whenever(mGMElasticSearchServiceAPI.getSearchResults("Kevin", "western", 30, 0, false)).thenReturn(Observable.just(mockSearchResponse))
        whenever(mGMElasticSearchServiceAPI.getSuggestResults("Kev", "western", 30, false)).thenReturn(Observable.just(mockSuggestResponse))
        mGMElasticSearchRepository = GMElasticSearchRepository.INSTANCE
        mGMElasticSearchRepository.gmElasticSearchServiceAPI = mGMElasticSearchServiceAPI
    }

    @Test fun testSuggest() {
        mGMElasticSearchRepository.suggest("Kev").subscribe { response ->
            response shouldBeInstanceOf  List::class
            response[0].apply {
                firstName  `should be equal to`  "Kevin"
                lastName `should be equal to` "McAdams"
                conferenceId `should be equal to` 7638248
            }
        }
    }

    @Test fun testSearch() {
        mGMElasticSearchRepository.search("Kevin").subscribe { response ->
            response shouldBeInstanceOf  List::class
            response[0].apply {
                firstName  `should be equal to`  "Kevin"
                lastName `should be equal to` "McAdams"
                conferenceId `should be equal to` 7638248
            }
        }
    }
}