package com.pgi.convergencemeetings.search.ui

import androidx.annotation.Nullable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pgi.convergence.constants.AppConstants
import com.pgi.convergence.enums.JoinMeetingEntryPoint
import com.pgi.convergencemeetings.greendao.ApplicationDao
import com.pgi.convergencemeetings.utils.AppAuthUtils
import com.pgi.convergencemeetings.meeting.gm5.ui.ViewModelResource
import com.pgi.network.models.ImeetingRoomInfo
import com.pgi.network.models.SearchResult
import com.pgi.network.repository.GMElasticSearchRepository
import io.reactivex.disposables.CompositeDisposable
import kotlin.text.RegexOption.IGNORE_CASE
import kotlin.text.RegexOption.MULTILINE

/**
 * This is the ViewModel for JoinMeetingFragment.
 * All the data needed for the JoinMeetingFragment will be provided by this model
 *
 * @author Sudheer R Chilumula
 * @since 5.17
 *
 * @see JoinMeetingFragment
 * @see ViewModel
 * @see SearchResult
 * @see ImeetingRoomInfo
 */
class JoinFragmentViewModel(
  val mAppAuthUtils: AppAuthUtils,
  val mApplicationDao: ApplicationDao,
  val gmElasticSearchRepository: GMElasticSearchRepository
) : ViewModel() {

  private val compositeDisposable = CompositeDisposable()

  val mSearchResponse: MutableLiveData<ViewModelResource<List<ImeetingRoomInfo>>> = MutableLiveData()
  var entryPoint: JoinMeetingEntryPoint = JoinMeetingEntryPoint.RECENT_MEETINGS

  /**
   * This will make a backend call to get the search results in case of host.
   * For guest we just filter the recent list and send back the filtered list.
   *
   * @param searchText  Text to search by
   */
  fun getSearchResults(searchText: String) {
    mSearchResponse.value = ViewModelResource.success(emptyList<SearchResult>())
    if (mAppAuthUtils.isUserTypeGuest) {
      val recentList = mApplicationDao.allRecentMeetings
      val filteredSearchResults = filterResultsOnSearchParams(searchText, recentList)
      if (filteredSearchResults != null) {
        mSearchResponse.value = ViewModelResource.success(filteredSearchResults)
      }
    } else {
      val searchDisposable = gmElasticSearchRepository.search(searchText).subscribe(
          { response ->
            mSearchResponse.value = ViewModelResource.success(response)
          },
          { error ->
            mSearchResponse.value = ViewModelResource.error(error)
          }
      )
      compositeDisposable.add(searchDisposable)
    }
  }

  /**
   * This will make a backend call to get the search results in case of host.
   * For guest we just filter the recent list and send back the filtered list.
   *
   * @param searchText  Text to search by
   * @param doGMSearch flag indicating whether to do the GM Search or not
   */
  fun getSuggestResults(searchText: String, doGMSearch: Boolean) {
    // for all users start with a search of their recent meetings
    val recentList = mApplicationDao.allRecentMeetings
    val urlMatchRegex = Regex(
      "((http|https)://)?[a-zA-Z0-9\\-.]+\\.[a-zA-Z]*(/\\S*)?", setOf(
        IGNORE_CASE, MULTILINE
      )
    )
    var filteredSearchResults = filterResultsOnSearchParams(searchText, recentList)
    var modifiedSearchText = searchText
    if (filteredSearchResults != null && filteredSearchResults.isNotEmpty()) {
      entryPoint = JoinMeetingEntryPoint.RECENT_MEETINGS
      var result = filteredSearchResults
      if(urlMatchRegex.matches(searchText)) {
        val tempSearchResult = SearchResult()
        if(!modifiedSearchText.contains(Regex("^(http|https)://", setOf(IGNORE_CASE)))) {
          modifiedSearchText = "https://" + searchText
        }
        tempSearchResult.furl = modifiedSearchText
        result = listOf(tempSearchResult) + filteredSearchResults
      }
      mSearchResponse.value = ViewModelResource.success(result)
    } else {
      var result = emptyList<SearchResult>()
      if(urlMatchRegex.matches(searchText)) {
        val tempSearchResult = SearchResult()
        if(!modifiedSearchText.contains(Regex("^(http|https)://", setOf(IGNORE_CASE)))) {
          modifiedSearchText = "https://" + searchText
        }
        tempSearchResult.furl = modifiedSearchText
        result = listOf(tempSearchResult)
      }
      mSearchResponse.value = ViewModelResource.success(result)
    }
    val searchParam = searchText.split(" ")
    val text = if (searchParam.size > 1) {
      val searchBuffer = StringBuilder()
      searchParam.forEach {
        if (it != "") {
          searchBuffer.append(it)
          if (it != searchParam.last()) {
            searchBuffer.append("+")
          }
        } else {
          val pos = searchBuffer.lastIndexOf("+")
          if (pos >= 0) {
            searchBuffer.deleteCharAt(pos)
          }
        }
      }
      searchBuffer.toString()
    } else modifiedSearchText
    if(urlMatchRegex.matches(text)) {
      entryPoint = JoinMeetingEntryPoint.URL_SEARCH
      val urldisposable = gmElasticSearchRepository.getMeetingRoomInfoFromFurl(text)
        .subscribe(
        { response ->
          if (filteredSearchResults != null) {
            filteredSearchResults += listOf(response)
            var result = filteredSearchResults.distinctBy { it.hubConfId }
            mSearchResponse.value = ViewModelResource.success(result)
          } else {
            mSearchResponse.value = ViewModelResource.success(listOf(response))
          }
        },
        { error ->
          mSearchResponse.value = ViewModelResource.error(error)
        }
      )
      compositeDisposable.add(urldisposable)
    } else if (doGMSearch){
      entryPoint = JoinMeetingEntryPoint.NAME_SEARCH
      val suggestDisposable = gmElasticSearchRepository.suggest(text).subscribe(
          { response ->
            if (filteredSearchResults != null) {
              filteredSearchResults += response
              var result = filteredSearchResults.distinctBy { it.hubConfId }
              mSearchResponse.value = ViewModelResource.success(result)
            } else {
              mSearchResponse.value = ViewModelResource.success(response)
            }
          },
          { error ->
            mSearchResponse.value = ViewModelResource.error(error)
          }
      )
      compositeDisposable.add(suggestDisposable)
    }
  }

  override fun onCleared() {
    super.onCleared()
    compositeDisposable.clear()
  }

  @Nullable
  fun filterResultsOnSearchParams(searchParam: String, searchResultList: List<SearchResult>?): List<SearchResult>? {
    var filteredSearchResultList: MutableList<SearchResult>? = null
    if (searchResultList != null && !searchResultList.isEmpty()) {
      filteredSearchResultList = ArrayList()
      for (searchResult in searchResultList) {
        val fullName = searchResult.firstName + AppConstants.BLANK_SPACE + searchResult.lastName
        if (searchResult.firstName != null && searchResult.firstName.toLowerCase().contains(searchParam.toLowerCase())
            || searchResult.lastName != null && searchResult.lastName.toLowerCase().contains(searchParam.toLowerCase())
            || searchResult.furl != null && searchResult.furl.toLowerCase().contains(searchParam.toLowerCase())
            || fullName != null && fullName.toLowerCase().contains(searchParam.toLowerCase())) {
          filteredSearchResultList.add(searchResult)
        }
      }
    }
    return filteredSearchResultList
  }
}