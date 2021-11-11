package com.pgi.network.repository

import com.pgi.convergence.utils.CommonUtils
import com.pgi.network.GMElasticSearchServiceManager
import com.pgi.network.helper.RetryAfterTimeoutWithDelay
import com.pgi.network.models.MeetingRoomInfo
import com.pgi.network.models.SearchResponse
import com.pgi.network.models.SearchResult
import com.pgi.network.models.SuggestResponse
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

/**
 * GMElasticSearchRepository is a singleton class that serves as an entry point to all GM Elastic Search APIS.
 *
 * @author Sudheer R Chilumula
 * @since 5.17
 * @see <a href="https://confluence.pgi-tools.com/pages/viewpage.action?spaceKey=IM&title=GMSearch&show-miniview">GM Elastic Search API</a>
 */
class GMElasticSearchRepository private constructor() : BaseRepository() {

	private object Holder {
		val INSTANCE = GMElasticSearchRepository()
	}

	companion object {
		val INSTANCE: GMElasticSearchRepository by lazy {
			Holder.INSTANCE
		}
	}

	private val gm5: String = "globalmeet5"
	private val resultSize = 30

	/**
	 * The nameOrder is set based on device locale. Currently only two values are supported (i.e. "western" and "eastern").
	 * "eastern" nameOrder currently is set only for Japanese.
	 */
	private val nameOrder: String = when (CommonUtils.isUsersLocaleJapan()) {
		true -> "eastern"
		false -> "western"
	}

	var gmElasticSearchServiceAPI: GMElasticSearchServiceAPI? = null

	/**
	 * We need to do this instead of lazy initialization as its not possible to mock lazy initialized values.
	 */
	// TODO:: Figure out how to mock lazy initializatied vars
	private fun getSearchService(): GMElasticSearchServiceAPI {
		return if (gmElasticSearchServiceAPI == null) {
			gmElasticSearchServiceAPI = GMElasticSearchServiceManager.create()
			gmElasticSearchServiceAPI!!
		} else {
			gmElasticSearchServiceAPI!!
		}
	}

	/**
	 * This is the search where we need to enter a complete first name or last name to get the
	 * search results. This doesn't support partial search.
	 * <p>
	 * The nameOrder required by api is set based on device current locale
	 * The resultSize is set by default to 30.
	 *
	 * In case of error we do retry 3 attempts before giving up and showing an error
	 *
	 * @param searchText       the text to be searched on
	 * @param from             Where in the search results to start from
	 * @param idsOnly          Return only the document IDs
	 * @return                 <code>Observable<List<SearchResult>></code>
	 *
	 * @see             Observable
	 * @see             SearchResponse
	 * @see             SearchResult
	 * @since           5.17
	 */
	fun search(searchText: String, from: Int = 0, idsOnly: Boolean = false): Observable<List<SearchResult>> {
		return getSearchService().getSearchResults(searchText, nameOrder, resultSize, from, idsOnly)
				.debounce(deBounceTimeout, TimeUnit.MILLISECONDS)
				.distinctUntilChanged()
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.flatMap { t: SearchResponse ->
					//TODO:: Clean up the old models. For now we are mapping the response to the old models so that it doesn't break functionality else where
					var searchList = emptyList<SearchResult>()
					for (item in t.items) {
						val name = item.details.title.split(" ")
						var searchResult = SearchResult()
						searchResult.hubConfId = item.id.toInt()
						searchResult.furl = item.details.externalUrl
						searchResult.conferenceId = item.details.metadata.conferenceId.toInt()
						searchResult.firstName = name[0]
						searchResult.lastName = name[1]
						searchResult.profileImageUrl = if (item.details.imageUrl != null) item.details.imageUrl else null
						searchResult.useHtml5 = (item.details.metadata.meetingRoomType == gm5)
						searchResult.brandId = if (item.details.metadata.brandId != null) item.details.metadata.brandId!!.toInt() else 0
						searchList += searchResult
					}
					Observable.just(searchList)
				}
				.retryWhen(RetryAfterTimeoutWithDelay(maxRetries, retryTimeout))
	}

	/**
	 * This is the search which supports partial matches. We need to enter a minimum of 3 letters to
	 * start getting response. The max results we can get using this api is 30.
	 * <p>
	 * The nameOrder required by api is set based on device current locale
	 * The resultSize is set by default to 30.
	 *
	 * In case of error we do retry 3 attempts before giving up and showing an error
	 *
	 * @param searchText       the text to be searched on
	 * @param highlight        Highlight matched text in search results
	 * @return                 <code>Observable<List<SearchResult>></code>
	 *
	 * @see             Observable
	 * @see             SuggestResponse
	 * @see             SearchResult
	 * @since           5.17
	 */
	fun suggest(searchText: String, highlight: Boolean = false): Observable<List<SearchResult>> {
		return getSearchService().getSuggestResults(searchText, nameOrder, resultSize, highlight)
				.debounce(deBounceTimeout, TimeUnit.MILLISECONDS)
				.distinctUntilChanged()
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.flatMap { t: List<SuggestResponse> ->
					//TODO:: Clean up the old models. For now we are mapping the response to the old models so that it doesn't break functionality else where
					var searchList = emptyList<SearchResult>()
					for (response in t) {
						val name = response.title.split(" ")
						var searchResult = SearchResult()
						searchResult.hubConfId = response.id.toInt()
						searchResult.furl = response.externalUrl
						searchResult.conferenceId = response.metadata.conferenceId.toInt()
						searchResult.firstName = name[0]
						searchResult.lastName = name[1]
						searchResult.profileImageUrl = if (response.imageUrl != null) response.imageUrl else null
						searchResult.useHtml5 = (response.metadata.meetingRoomType == gm5)
						searchResult.brandId = if (response.metadata.brandId != null) response.metadata.brandId!!.toInt() else 0
						searchList += searchResult
					}
					Observable.just(searchList)
				}
				.retryWhen(RetryAfterTimeoutWithDelay(maxRetries, retryTimeout))
	}

	//    fun getMeetingRoomInfoFromRoomID(roomId: Int, idsOnly: Boolean = false): Observable<MeetingRoomInfo> {
	//        return mGmElasticSearchServiceAPI.getMeetingRoomInfoFromRoomID(roomId, idsOnly)
	//                .subscribeOn(Schedulers.io())
	//                .observeOn(AndroidSchedulers.mainThread())
	//                .retryWhen(RetryAfterTimeoutWithDelay(maxRetries, AppConstants.RETRY_1000_MS.toLong()))
	//    }

	/**
	 * This is to get the meeting room info from the FURL
	 * This method  works only for Hosts.
	 *
	 * In case of error we do retry 3 attempts before giving up and showing an error
	 *
	 * @param furl       the room url we need info for
	 * @param idsOnly    Return only the document IDs
	 * @return           <code>Observable<SearchResult></code>
	 *
	 * @see             Observable
	 * @see             MeetingRoomInfo
	 * @see             SearchResult
	 * @since           5.17
	 */
	fun getMeetingRoomInfoFromFurl(furl: String, idsOnly: Boolean = false): Observable<SearchResult> {
		return getSearchService().getMeetingRoomInfoFromFURL(furl)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.flatMap { t: MeetingRoomInfo ->
					//TODO:: Clean up the old models. For now we are mapping the response to the old models so that it doesn't break functionality else where
					var searchResult = SearchResult()
					searchResult.hubConfId = t.id.toInt()
					searchResult.furl = t.details.meetingRoomUrl
					searchResult.conferenceId = t.details.conferenceId.toInt()
					searchResult.firstName = t.details.ownerGivenName
					searchResult.lastName = t.details.ownerFamilyName
					searchResult.profileImageUrl = if (t.details.images.isNotEmpty()) t.details.images[0].imageUrl else null
					searchResult.useHtml5 = (t.details.meetingRoomType == gm5)
					searchResult.brandId = if (t.details.brandId != null) t.details.brandId!!.toInt() else 0
					Observable.just(searchResult)
				}
				.retryWhen(RetryAfterTimeoutWithDelay(1, retryTimeout))
	}
}