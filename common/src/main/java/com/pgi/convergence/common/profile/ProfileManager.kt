package com.pgi.convergence.common.profile

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pgi.logging.Logger
import com.pgi.logging.enums.LogEvent
import com.pgi.logging.enums.LogEventValue
import com.pgi.network.models.SearchResult
import com.pgi.network.repository.GMElasticSearchRepository
import com.pgi.network.viewmodels.ViewModelResource
import io.reactivex.disposables.CompositeDisposable
import kotlinx.serialization.UnstableDefault
import retrofit2.HttpException

class ProfileManager @UnstableDefault constructor(
    val gmElasticSearchRepository: GMElasticSearchRepository,
		val logger: Logger) {

	private val TAG = ProfileManager::class.java.name
	val compositeDisposable = CompositeDisposable()
	var lastFirstVisiblePosition: Int = 0
	val mSearchResponse: MutableLiveData<ViewModelResource<List<SearchResult>>> = MutableLiveData()
	var recyclerView: RecyclerView? = null
	var lifecycleOwner: LifecycleOwner? = null
	var activity: FragmentActivity? = null


	fun getSuggestResults(searchText: String, map: MutableMap<String, String>, noProfileImageMap: MutableMap<String, String>) {
		if (!map.containsKey(searchText) && !noProfileImageMap.containsKey(searchText)) {
			val suggestDisposable = gmElasticSearchRepository.suggest(searchText).subscribe(
					{ response ->
						mSearchResponse.value = ViewModelResource.success(response)
						map.put(searchText, response[0].profileImageUrl)
					},
					{ error ->
						mSearchResponse.value = ViewModelResource.error(error)
						noProfileImageMap.put(searchText, error.localizedMessage)
					}
																																										 )
			compositeDisposable.add(suggestDisposable)
		}

	}

	fun registerViewModelListener() {
		lifecycleOwner?.let {
			mSearchResponse.observe(it, androidx.lifecycle.Observer { viewModelResource: ViewModelResource<List<SearchResult>> ->
				when (viewModelResource.status) {
					ViewModelResource.Status.SUCCESS -> {
						recyclerView?.runOnUiThread()
					}
					ViewModelResource.Status.ERROR ->
						if (viewModelResource.exception is HttpException) {
							val ex = viewModelResource.exception as HttpException?
							logger.error(TAG, LogEvent.EXCEPTION, LogEventValue.PROFILEIMAGESEARCH, ex?.message.toString(), ex)
						}
				}
			})
		}
	}

	fun RecyclerView.runOnUiThread() {
		activity?.runOnUiThread(java.lang.Runnable {
			saveScrollPosition()
			this.adapter?.notifyDataSetChanged()
			restoreScrollState()
		})
	}

	// Restores the scroll state of the recycleview
	fun RecyclerView.restoreScrollState() {
		val linearlayoutManager: LinearLayoutManager = this.layoutManager as LinearLayoutManager
		linearlayoutManager.scrollToPositionWithOffset(lastFirstVisiblePosition, 0);
	}

	// Saves the scroll state of the recycleview
	fun RecyclerView.saveScrollPosition() {
		val linearlayoutManager: LinearLayoutManager = this.layoutManager as LinearLayoutManager
		lastFirstVisiblePosition = linearlayoutManager.findFirstCompletelyVisibleItemPosition();
	}
}