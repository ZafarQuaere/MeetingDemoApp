package com.pgi.convergencemeetings.search.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView

import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.newrelic.agent.android.NewRelic
import com.pgi.convergence.constants.AppConstants
import com.pgi.convergence.utils.CommonUtils
import com.pgi.convergencemeetings.R
import com.pgi.convergencemeetings.base.ui.BaseActivity
import com.pgi.network.models.ImeetingRoomInfo
import com.pgi.convergencemeetings.base.services.retry_callbacks.RetryStatus
import com.pgi.convergencemeetings.utils.ClientInfoResultCache
import com.pgi.convergencemeetings.utils.JoinMeetingsListener
import com.pgi.convergencemeetings.meeting.gm5.ui.ViewModelResource
import com.pgi.logging.enums.Interactions
import com.pgi.logging.enums.LogEvent
import com.pgi.logging.enums.LogEventValue

import butterknife.BindView
import butterknife.ButterKnife
import com.pgi.convergence.application.CoreApplication
import com.pgi.convergence.enums.JoinMeetingEntryPoint.*
import com.pgi.convergencemeetings.greendao.ApplicationDao
import retrofit2.HttpException

import com.pgi.convergencemeetings.utils.ClientInfoDaoUtils
import com.pgi.network.models.SearchResult
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/**
 * A DialInFragmentContract representing a list of Recent meeting items.
 *
 *
 * Activities containing this DialInFragmentContract MUST implement the [JoinMeetingsListener]
 * interface.
 * Handling the UI visiblity for recent meeting list data presence and absense.
 */
/**
 * Mandatory empty constructor for the DialInFragmentContract manager to instantiate the
 * DialInFragmentContract (e.g. upon screen orientation changes).
 */
class JoinMeetingFragment : Fragment(), JoinMeetingContractor.View, JoinMeetingsListener {
  private val mLogger = CoreApplication.mLogger
  val viewModel: JoinFragmentViewModel by sharedViewModel()
  var meetingRoomsAdapter: JoinMeetingsAdapter? = null
  private lateinit var mJoinMeetingsPresenter: JoinMeetingsPresenter
  private var mLinearLayoutManager: LinearLayoutManager? = null
  private var mRecentSearchResults: List<ImeetingRoomInfo> = emptyList()
  private var mMeetingRoomSearchResults: List<ImeetingRoomInfo> = emptyList()
  @BindView(R.id.rv_recent_meetings)
  lateinit var mRecentMeetingsRecyclerView: RecyclerView
  @BindView(R.id.progress_recent_meeting)
  lateinit var pbRecentMeeting: ProgressBar
  @BindView(R.id.ll_recent_meetings)
  lateinit var llRecentMeetings: LinearLayout
  @BindView(R.id.ll_no_recent_data)
  lateinit var llNoMeetingView: LinearLayout
  @BindView(R.id.tv_recently_joined)
  lateinit var tvRecentlyJoined: TextView
  @BindView(R.id.tv_no_recent_data)
  lateinit var tvNoSearchResults: TextView
  private var mClientId: String? = null
  internal var reason: Int = 0
  internal var value: Int = 0

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    NewRelic.setInteractionName(Interactions.SEARCH.interaction)
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {

    val view = inflater.inflate(R.layout.fragment_join_meeting, container, false)
    mJoinMeetingsPresenter = JoinMeetingsPresenter(activity, this)
    mLinearLayoutManager = LinearLayoutManager(view.context)
    ButterKnife.bind(this, view)
    registerViewModelListener()
    handleRecyclerViewTouchEvent()
    val bundle = arguments
    if (bundle != null) {
      mClientId = bundle.getString(AppConstants.CLIENT_ID)
    }
    return view
  }

  private fun registerViewModelListener() {
    viewModel.mSearchResponse.observe(this, Observer {
      viewModelResource: ViewModelResource<List<ImeetingRoomInfo>> ->
      when (viewModelResource.status) {
        ViewModelResource.Status.SUCCESS -> {
          viewModelResource.data?.let { notifyAdapterOnSearchUpdate(it) }
        }

        ViewModelResource.Status.LOADING -> {
          notifyAdapterOnRecentUpdate(mRecentSearchResults)
        }

        ViewModelResource.Status.ERROR -> if (viewModelResource.exception is HttpException) {
          val ex = viewModelResource.exception as HttpException?
          if(ex != null) {
            val mRetryStatus = RetryStatus(ex.code(), RetryStatus.WS_ENTERPRISE_SEARCH)
            if (!ex.code().toString().startsWith(AppConstants.CODE_4) && ex.code().toString().startsWith(AppConstants.CODE_2)) {
              onServiceRetryFailedParent(mRetryStatus)
            }
          }
        }
      }
      })
  }

  override fun onResume() {
    super.onResume()
    mJoinMeetingsPresenter.loadLocalData()
    val clientInfoDaoutils = ClientInfoDaoUtils.getInstance()
    clientInfoDaoutils.refereshClientInfoResultDao()
    val mClientId = clientInfoDaoutils.clientId
    if (mClientId != null && CommonUtils.isConnectionAvailable(activity)) {
      //Load the recent meetings...
      mJoinMeetingsPresenter.getRecentMeetingInfo(mClientId)
    }
  }

  /**
   * Updating the recent join meeting list in Recyclerview and hiding the NoSerachView if list having meeting objects.
   *
   * @param searchResults list containing meeting room info
   */
  override fun notifyAdapterOnRecentUpdate(searchResults: List<ImeetingRoomInfo>?) {
    mRecentSearchResults = searchResults ?: emptyList()
    if (!searchResults.isNullOrEmpty()) {
      hideProgress()
      mRecentMeetingsRecyclerView.layoutManager = mLinearLayoutManager
      val joinMeetingsAdapter = JoinMeetingsAdapter(searchResults, this)
      mRecentMeetingsRecyclerView.adapter = joinMeetingsAdapter
      joinMeetingsAdapter.notifyDataSetChanged()
      llNoMeetingView.visibility = View.GONE
      llRecentMeetings.visibility = View.VISIBLE
      tvRecentlyJoined.text = activity?.getString(R.string.title_recently_joined)
    } else {
      displayNoRecentMeetingUI()
    }
  }

  /**
   * Updating the Recyclerview after searching the meeting, which contain recent meeting join history.
   * -If no result available then showing NoRecentMeeting view instead of recent meeting history list.
   *
   * @param roomResults List containing meeting room info
   */
  override fun notifyAdapterOnSearchUpdate(roomResults: List<ImeetingRoomInfo>?) {
    mMeetingRoomSearchResults = roomResults ?: emptyList()
    if (roomResults != null && roomResults.size > 0) {
      hideProgress()
      mRecentMeetingsRecyclerView.layoutManager = mLinearLayoutManager
      val meetingRoomsAdapter = JoinMeetingsAdapter(roomResults, this)
      mRecentMeetingsRecyclerView.adapter = meetingRoomsAdapter
      this.meetingRoomsAdapter = meetingRoomsAdapter
      meetingRoomsAdapter.notifyDataSetChanged()
      llNoMeetingView.visibility = View.GONE
      tvRecentlyJoined.text = activity?.getString(R.string.search_join_header)
      llRecentMeetings.visibility = View.VISIBLE
    } else {
      displayNoSearchResutsUI()
    }
  }

  private fun handleRecyclerViewTouchEvent() {
    if (mRecentMeetingsRecyclerView != null) {
      mRecentMeetingsRecyclerView.setOnTouchListener({ view, motionEvent ->
        CommonUtils.hideKeyboard(activity)
        false
      })
    }
  }

  override fun onPause() {
    super.onPause()
    CommonUtils.hideKeyboard(activity)
  }

  override fun onDetach() {
    super.onDetach()
  }

  override fun showProgress() {
    llNoMeetingView.visibility = View.GONE
    pbRecentMeeting.visibility = android.view.View.VISIBLE
  }

  override fun hideProgress() {
    pbRecentMeeting.visibility = android.view.View.GONE
  }

  override fun onServiceRetryFailedParent(retryStatus: RetryStatus) {
    try {
      val baseActivity = activity as BaseActivity?
      baseActivity?.onServiceRetryFailed(retryStatus)
    } catch (ex: Exception) {
      mLogger.error(
        TAG, LogEvent.EXCEPTION, LogEventValue.JOIN,
        "JoinMeetingFragment onServiceRetryFailedParent() - Retrying Service failed",
        ex, null, true, false
      )
    }

  }

  override fun onRecentMeetingInfoError(errorMsg: String?, response: Int) {
    if (AppConstants.RECENT_MEETING_SEARCH_ISSUE.equals(errorMsg, ignoreCase = true)) {
      //DO nothing if get this error message, for recent search
      //It happens for the accounts doesn't have any recent meetings.
    } else {
      reason = RetryStatus.WS_CLIENT_INFO_SELF
      value = response
      val retryStatus = RetryStatus(reason, value)
      onServiceRetryFailedParent(retryStatus)
    }
  }


  override fun onRecentMeetingClick(item: ImeetingRoomInfo?) {
    if (item != null) {
      val meetingRoomId: String? = item.meetingRoomId
      val isUseHtml5 = item.isUseHtml5
      val meetingConferenceID: String? = item.meetingConferenceId
      val furl = item.furl
      ClientInfoResultCache.getInstance().selectedMeetingRoomId = meetingRoomId
      if (furl != null && (meetingRoomId?.isEmpty() == false && meetingRoomId != "null")) {
        if (viewModel.entryPoint == RECENT_MEETINGS) {
          mLogger.record(LogEvent.FEATURE_JOINFROMRECENTS)
        } else {
          mLogger.record(LogEvent.FEATURE_JOINFROMSEARCH)
        }

        (this.activity as BaseActivity).launchAudioSelectionActivity(
          context,
          meetingConferenceID,
          furl,
          item.hostFirstName,
          isUseHtml5,
          System.currentTimeMillis(),
          viewModel.entryPoint,
          !viewModel.mAppAuthUtils.isUserTypeGuest, item as? SearchResult
        )
      } else {
        (activity as BaseActivity).showInvalidConferenceAlert(activity)
        Log.d(TAG, CONFERENCE_ID_IS_NULL_CANNOT_LAUNCH_MEETING_ROOM)
      }
    } else {
      Log.d(TAG, MEETING_FROM_INFO_IS_NULL_CANNOT_START_MEETING)
    }
  }

  /**
   * Updating the UI if No recent meetings availble
   */
  private fun displayNoRecentMeetingUI() {
    llRecentMeetings.visibility = View.GONE
    llNoMeetingView.visibility = View.VISIBLE
    tvNoSearchResults.text = activity?.getString(R.string.no_recent_meetings)
  }

  private fun displayNoSearchResutsUI() {
    llRecentMeetings.visibility = View.GONE
    llNoMeetingView.visibility = View.VISIBLE
    tvNoSearchResults.text = activity?.getString(R.string.no_results)
  }

  companion object {
    private val TAG = JoinMeetingFragment::class.java.simpleName
    val CONFERENCE_ID_IS_NULL_CANNOT_LAUNCH_MEETING_ROOM =
      "conferenceId is null cannot launch meeting room"
    val MEETING_FROM_INFO_IS_NULL_CANNOT_START_MEETING =
      "meeting from info is null cannot start meeting"
    private val noOfRetries = 0
  }

}
