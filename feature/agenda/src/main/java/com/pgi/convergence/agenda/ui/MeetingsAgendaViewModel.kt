package com.pgi.convergence.agenda.ui

import android.app.Activity
import android.app.PendingIntent
import android.content.*
import android.content.Intent.*
import android.graphics.Rect
import android.net.Uri
import android.provider.CalendarContract
import android.view.TouchDelegate
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat.*
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.pgi.convergence.agenda.BuildConfig
import com.pgi.convergence.agenda.R
import com.pgi.convergence.broadcastreceivers.ShareSheetBroadcastReceiverInOutMeeting
import com.pgi.convergence.common.profile.ProfileManager
import com.pgi.convergence.constants.AppConstants
import com.pgi.convergence.data.enums.home.HomeCardType
import com.pgi.convergence.data.enums.home.HomeCardType.WELCOME_CARD
import com.pgi.convergence.data.enums.msal.MsalMeetingRoom.GMROOM
import com.pgi.convergence.data.enums.msal.MsalMeetingStatus
import com.pgi.convergence.data.model.home.CardProfile
import com.pgi.convergence.data.model.home.HomeCardData
import com.pgi.convergence.data.repository.msal.MSALAuthRespositoryImpl
import com.pgi.convergence.data.repository.msal.MSALGraphRepositoryImpl
import com.pgi.convergence.dialogs.MeetingNotFoundDialog
import com.pgi.convergence.persistance.SharedPreferencesManager
import com.pgi.convergence.ui.SharedViewModel
import com.pgi.convergence.utils.CommonUtils
import com.pgi.convergence.utils.FeaturesUtil
import com.pgi.convergence.utils.InternetConnection
import com.pgi.logging.Logger
import com.pgi.logging.enums.LogEvent
import com.pgi.logging.enums.LogEventValue
import com.pgi.network.models.GMMeetingInfoGetResponse
import com.pgi.network.models.MeetingRoomGetResult
import com.pgi.network.models.SearchResult
import com.pgi.network.models.localizedPhoneTye
import com.pgi.network.repository.GMWebServiceRepository
import com.pgi.network.viewmodels.ViewModelResource
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_meetings.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.serialization.UnstableDefault
import org.jetbrains.annotations.TestOnly
import org.joda.time.LocalDate
import java.util.*
import kotlin.concurrent.fixedRateTimer
import kotlin.text.StringBuilder

@UnstableDefault
@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
class MeetingsAgendaViewModel(val authRepository: MSALAuthRespositoryImpl,
                              val graphRepository: MSALGraphRepositoryImpl,
                              val sharedViewModel: SharedViewModel,
                              val sharedPref: SharedPreferencesManager,
                              val profileManager: ProfileManager,
                              val logger: Logger,
                              val gmWebServiceRepository: GMWebServiceRepository) : ViewModel() {

  private val TAG = MeetingsAgendaViewModel::class.java.name
  var invokedRedirect = false
  var firstName: String? = null
  var lastName: String? = null
  var initials: String? = null
  var fullName: String? = null
  var confId: String? = null
  var profileImage: String? = null
  val joinUrlData: MutableLiveData<String> by lazy { MutableLiveData<String>("") }
  var joinUrl: LiveData<String> = joinUrlData
  var meetingRoomName: String? = null
  var overflowMenuDialogFragmentCtx = OverflowMenuDialogFragment()
  var clip: ClipData? = null
  var clipboard: ClipboardManager? = null
  val mSearchResponse: MutableLiveData<ViewModelResource<List<SearchResult>>> = MutableLiveData()
  var searchResults: List<SearchResult>? = null
  private val mSharedPreferencesManager = SharedPreferencesManager.getInstance()
  private val mMeetingCardTransitionIntervalInMin: Int = 15
  private val mCardRefreshInterval = 30000L
  private val maxResults = 50
  private val msalViewModel by lazy { sharedViewModel.msalModel }
  private val meetingNotFoundDialog = MeetingNotFoundDialog()
  private var agendaDataTimer: Timer? = null
  val noProfileImageMap: MutableMap<String, String> = mutableMapOf()
  private var coroutineExceptionHandler: (LogEventValue, String) -> CoroutineExceptionHandler =
      { logEventValue: LogEventValue, method: String ->
        CoroutineExceptionHandler { _, exception ->
          GlobalScope.launch {
            logger.error(TAG, LogEvent.EXCEPTION, logEventValue, "HomeCardsViewModel: $method", exception)
          }
        }
      }
  private val agendaDataList: MutableLiveData<List<HomeCardData>> by lazy { MutableLiveData<List<HomeCardData>>() }
  var agendaData: LiveData<List<HomeCardData>> = agendaDataList
  val launchUrl: MutableLiveData<String> by lazy { MutableLiveData<String>() }
  val loadingData: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>(false) }
  val isLoading: LiveData<Boolean> = loadingData

  var office365Token: String? = null
  var mAgendaList = Collections.emptyList<HomeCardData>()
    private set
  var updatedAgenda = Collections.emptyList<HomeCardData>()
  var newAgenda = emptyList<HomeCardData>()
  var meetingsFragment = MeetingsFragment()
  var lastFirstVisiblePosition: Int = 0
  private var meetingsAgendaAdapter = MeetingsAgendaAdapter(R.layout.agenda_recycleview_cells, this)
  private val compositeDisposable = CompositeDisposable()
  val map: MutableMap<String, String> = mutableMapOf()
  val noProfileMap: MutableMap<String, String> = mutableMapOf()

  var appComponent: Context? = null
    set(value) {
      field = value
      msalViewModel.context = value
      msalViewModel.packagaName = value?.packageName
      authRepository.mActivity = value
    }
  var mRoomId: String = ""
  var mIsLoading: Boolean = false
  var mMeetingRoomInfo: GMMeetingInfoGetResponse? = null
  var flavor = BuildConfig.FLAVOR

  fun getMeetingsAgendaAdapter(): MeetingsAgendaAdapter? {
    return meetingsAgendaAdapter
  }

  fun setDialogFragment(overflowMenuDialogFragment: OverflowMenuDialogFragment) {
    overflowMenuDialogFragmentCtx = overflowMenuDialogFragment
  }

  fun loadProfileImageOrText(circleImageView: CircleImageView?) {
    if (profileImage != null) {
      try {
        Picasso.get()
            .load(profileImage)
            .fit()
            .into(circleImageView)
      } catch (e: Exception) {
        logger.error(TAG, LogEvent.EXCEPTION, LogEventValue.HOME, e.message.toString(), e)
      }
    }
  }

  fun loadProfileImageHome(circleImageView: CircleImageView?, string: String?) {
    if (string != null) {
      Picasso.get()
          .load(string)
          .fit()
          .into(circleImageView)
    }
  }

  fun onOverFlowMenuClicked() {
    val overflowMenuDialogFragment = OverflowMenuDialogFragment()
    try {
      appComponent.let {
        val context = it as FragmentActivity
        overflowMenuDialogFragment.show(context.supportFragmentManager,
            "overflow_menu_dialog_fragment")
      }
    } catch (e: Exception) {
      logger.error(TAG, LogEvent.EXCEPTION, LogEventValue.HOME, e.message.toString(), e)
    }
  }

  @TestOnly
  fun onClick(v: View?) {
    when (v?.id) {
      R.id.rl_meeting_container_view -> {
        startMeeting()
      }
      R.id.iv_overflow_meetings -> {
        onOverFlowMenuClicked()
      }
      R.id.tv_join_my_meeting -> {
        dismissDialog()
        startMeeting()
      }
      R.id.tv_copy_meeting_url -> {
        copyRoomURL()
        dismissDialog()
      }
      R.id.tv_schedule_a_meeting -> {
        launchCalendar()
      }
      R.id.tv_share_meeting_url-> {
        shareMeetingURL()
        dismissDialog()
      }
    }
  }


  fun startMeetingAndDismissDialog() {
    dismissDialog()
    startMeeting()
  }

  fun copyUrlAndDismissDialog() {
    copyRoomURL()
    dismissDialog()
  }

  fun launchCalendar() {
    dismissDialog()
    val context = appComponent ?: return

    if (!InternetConnection.isConnected(context)) {
      Toast.makeText(context, R.string.no_network, Toast.LENGTH_SHORT).show()
      return
    }

    mMeetingRoomInfo?.let {
      scheduleMeeting(it)
    } ?: run {
      getMeetingRoomInfo(true)
    }

  }

  fun scheduleMeeting(meetingRoomResult: GMMeetingInfoGetResponse?) {
    appComponent?.let { context ->
      val description = getCalendarDescription(meetingRoomResult?.meetingRoomGetResult, context)
      val intent = Intent(Intent.ACTION_INSERT)
              .setData(CalendarContract.Events.CONTENT_URI)
              .putExtra(CalendarContract.Events.EVENT_LOCATION, joinUrl.value.toString())
              .putExtra(CalendarContract.Events.DESCRIPTION, description)
      val intentToLaunch = filterCalendarIntent(intent, context)
      try {
        intentToLaunch?.let {
          logger.info(TAG, LogEvent.MIXPANEL_EVENT, LogEventValue.MIXPANEL_SCHEDULE, "Mixpanel Event: " + LogEventValue.MIXPANEL_SCHEDULE.value,
                  null, null, false, true)
          (context as? FragmentActivity)?.startActivity(intentToLaunch)
        } ?: run {
          Toast.makeText(context, R.string.no_calendar_app_available, Toast.LENGTH_SHORT).show()
        }
      } catch (e: Exception) {
        logger.error(TAG, LogEvent.EXCEPTION, LogEventValue.HOME, e.message.toString(), e)
      }
    }
  }

  private fun filterCalendarIntent(intent: Intent, context: Context): Intent? {
    val resInfos = context.packageManager.queryIntentActivities(intent, 0)
    val intentList = arrayListOf<Intent>()
    for (resInfo in resInfos) {
      val packageName = resInfo.activityInfo.packageName
      if (!packageName.toLowerCase().contains(AppConstants.OUTLOOK_APP_PACKAGE_NAME)) {
        val calendarIntent = Intent(intent)
        calendarIntent.component = ComponentName(packageName, resInfo.activityInfo.name)
        intentList.add(calendarIntent)
      }
    }
    var intentToLaunch: Intent? = null
    if (intentList.isNotEmpty()) {
      val intentChooser = Intent.createChooser(Intent(), context.getString(R.string.open_with))
      intentChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentList.toTypedArray())
      intentToLaunch = intentChooser
    }
    return intentToLaunch
  }

  private fun getCalendarDescription(meetingInfoResult: MeetingRoomGetResult?, context: Context): String {
    val stringBuilder = StringBuilder(String.format(context.getString(R.string.schedule_meeting_named_header), fullName + "'s"))
    stringBuilder.append(":\n" +
            " ${joinUrl.value}\n" +
            "\n")
    meetingInfoResult?.audioDetail?.let {
        if (!it.phoneInformation.isNullOrEmpty()) {
          stringBuilder.append(context.getString(R.string.or_dial_in_using_phone_label) + "\n")

          val primaryNumber = it.primaryAccessNumber
          val passCode = it.participantPassCode
          if (primaryNumber.isNotEmpty()) {
            // Using regex to remove spaces in the phone number if exists
            stringBuilder.append(" ${context.getString(R.string.mobile)}: tel:${primaryNumber.replace("\\s".toRegex(), "")},,,${passCode}#\n")
            stringBuilder.append(" ${context.getString(R.string.access)} #: ${primaryNumber}\n")
          }

          if (passCode.isNotEmpty()) {
            stringBuilder.append(" ${context.getString(R.string.guest_passcode)}: ${passCode}\n")
          }

          val phoneNumbers = it.phoneInformation.filter { it.phoneNumber != primaryNumber }
          stringBuilder.append("\n${context.getString(R.string.other_access_numbers)}:\n")
          phoneNumbers.forEach { phoneNumber ->
            stringBuilder.append(phoneNumber.localizedPhoneTye(context) + ", ")
            stringBuilder.append(phoneNumber.location + " " + phoneNumber.phoneNumber + "\n")
          }

          val enableVRC = meetingInfoResult.meetingRoomDetail?.enableVrc ?: false
          val vrcURL = meetingInfoResult.meetingRoomUrls?.vrcUrl ?: AppConstants.EMPTY_STRING
          if (enableVRC && vrcURL.isNotEmpty()) {
            stringBuilder.append("\n" +
                    "${context.getString(R.string.join_using_sip_header)}\n")
            stringBuilder.append(" ${context.getString(R.string.sip_uri)} & ${context.getString(R.string.h323_address_details)}: $vrcURL")
          }
        }
    }
    return stringBuilder.toString()
  }

  fun startMeeting() {
    if (!joinUrl.value.isNullOrEmpty()) {
      launchUrl.postValue(mSharedPreferencesManager.joinUrl)
    }
  }

  fun populateRoomData() {
    joinUrlData.postValue(mSharedPreferencesManager.joinUrl)
    firstName = mSharedPreferencesManager.firstName
    lastName = mSharedPreferencesManager.lastName
    profileImage = mSharedPreferencesManager.profileImage
    initials = CommonUtils.getNameInitials(firstName, lastName)
    fullName = CommonUtils.getFullName(firstName, lastName)
    meetingRoomName = CommonUtils.formatMeetingRoomName(firstName, lastName)
    confId = mSharedPreferencesManager.confId

  }

  fun dismissDialog() {
    if (overflowMenuDialogFragmentCtx.fragmentManager != null) {
      overflowMenuDialogFragmentCtx.dismiss()
    }
  }

  fun copyRoomURL() {
    appComponent?.let { it ->
      CommonUtils.copyRoomURL(it, joinUrl.value.toString(), AppConstants.COPY_URL)
    }
  }

  fun expandTouchArea(bigView: View, smallView: View, extraPadding: Int) {
    bigView.post {
      val rect = Rect()
      smallView.getHitRect(rect)
      rect.top -= extraPadding
      rect.left -= extraPadding
      rect.right -= extraPadding
      rect.bottom += extraPadding
      bigView.touchDelegate = TouchDelegate(rect, smallView)
    }
  }

  fun checkIfMsalUserisAutheticated(): Boolean {
    return authRepository.getUserCount() != 0
  }

  fun authenticateMsalUser() {
    viewModelScope.launch(
        Dispatchers.Main + coroutineExceptionHandler(
            LogEventValue.MSAL_AUTH,
            "authenticateMsalUser()"
        )
    )
    {
      authRepository.authenticateUser()
    }
    invokedRedirect = true
  }

  fun registerMsalTokenChannel() {
    viewModelScope.launch(
        Dispatchers.Main + coroutineExceptionHandler(
            LogEventValue.MSAL_AUTH,
            "registerMsalTokenChannel()"
        )
    ) {
      authRepository.msalAccessTokenChannel.consumeEach {
        office365Token = it
        if (office365Token != null) {
          if (sharedPref.isfirstTimeMsalUser()) {
            sharedPref.firstTimeMsalUser(false)
            triggerAgendaDataTimer()
          } else {
            getCalendarData()
          }
        } else {
          sharedPref.firstTimeMsalUser(true)
          onAgendaUpdateInterval()
        }
      }
    }
  }

  fun unRegisterMsalTokenChannel() {
    viewModelScope.launch(
        Dispatchers.Main + coroutineExceptionHandler(
            LogEventValue.MSAL_AUTH,
            "unRegisterMsalTokenChannel()"
        )
    ) {
      authRepository.msalAccessTokenChannel.close()
    }
  }

  fun handleInteractiveRequestRedirect(requestCode: Int, resultCode: Int, data: Intent?) {
    authRepository.handleInteractiveRequestRedirect(requestCode, resultCode, data)
  }

  fun triggerAgendaDataTimer() {
    agendaDataTimer = fixedRateTimer(
        "updateCardData",
        false,
        0L,
        mCardRefreshInterval
    ) {
      onAgendaUpdateInterval()
    }
  }

  fun cancelCardDataTimer() {
    agendaDataTimer?.cancel()
    agendaDataTimer?.purge()
    agendaDataTimer = null
  }

  internal fun getFirstTimeUserWelcomeCardData() {
    try {
      mAgendaList = msalViewModel.getFirstTimeUserCardData()
    } catch (e: Exception) {
      logger.error(TAG, LogEvent.EXCEPTION, LogEventValue.HOME, e.message.toString(), e)
    }
    sortAgendaDataAndUpdate()
  }

  private fun onAgendaUpdateInterval() {
    retreiveAgendaData()
  }

  private fun retreiveAgendaData() {
    if (sharedPref.isfirstTimeMsalUser()) {
      getFirstTimeUserWelcomeCardData()
      loadingData.postValue(false)
      cancelCardDataTimer()
    } else {
      getCalendarData()
    }
  }

  fun startLoading() {
    getDummyData()
    loadingData.postValue(true)
  }

  private fun getDummyData() {
    sortAgendaDataAndUpdate()
    mAgendaList = listOf(
        HomeCardData(
            "1",
            HomeCardType.GUEST_JOIN_NOW_MEETINGS,
            MsalMeetingStatus.NONE,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            false
        )
    )
    sortAgendaDataAndUpdate()
  }

  private fun getCalendarData() {
    viewModelScope.launch(
        Dispatchers.IO + coroutineExceptionHandler(
            LogEventValue.MSAL_GETEVENTS,
            "getCalendarData()"
        )
    ) {
      if (office365Token == null || authRepository.isTokenExpired()) {
        authenticateMsalUser()
      } else if (checkIfMsalUserisAutheticated()) {
        val start = LocalDate.now().toDateTimeAtCurrentTime().minusMinutes(mMeetingCardTransitionIntervalInMin)
        val end = LocalDate.now().plusDays(1).toDateTimeAtStartOfDay().minusSeconds(1)
        office365Token?.let {
          val events =
              graphRepository.getTimedEvents(
                  it, start.toString(), end
                  .toString(), maxResults
              )
          mAgendaList = msalViewModel.parseMsalCalEvent(events, true)
          sortAgendaDataAndUpdate()
          loadingData.postValue(false)
        }
      }
    }
  }

  fun isAgendaEmpty(): Boolean {
    return updatedAgenda.isEmpty()
  }

  fun getAgendaCellAtIndex(position: Int): HomeCardData {
    return updatedAgenda[position]
  }


  fun sortAgendaDataAndUpdate() {
    newAgenda = updatedAgenda
    updatedAgenda = Collections.emptyList()
    updatedAgenda = mAgendaList.filter {
      it.type != HomeCardType.FOLLOW_UP_MEETINGS
    }
    meetingsAgendaAdapter.agenda = updatedAgenda
    agendaDataList.postValue(updatedAgenda)

    if (!sharedPref.isfirstTimeMsalUser()) {
      if (!newAgenda.isNullOrEmpty()) {
        runOnUiThread(newAgenda)
      } else {
        runOnUiThread(updatedAgenda)
      }

    }

  }

  fun runOnUiThread(cardData: List<HomeCardData>) {
    if(cardData.isNotEmpty()) {
      meetingsFragment.activity?.runOnUiThread {
        saveScrollPosition()
        meetingsAgendaAdapter.notifyDataSetChanged()
        restoreScrollState()
      }
    }
  }


  fun joinMeeting(type: HomeCardType, link: String?, profile: CardProfile?) {
    if (type == WELCOME_CARD) {
      authenticateMsalUser()
    } else {
      if (!link.isNullOrEmpty() && profile != null && profile.subhead != null && profile.subhead?.isUrl!!) {

        if (profile.subhead?.meetingRoom == GMROOM  && FeaturesUtil.checkLaunchUrlDomainMatchesPackage(flavor, link)) {
          launchUrl.postValue(link)
        } else {
          appComponent?.let {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(link)
            try {
              startActivity(it, intent, null)

            } catch (e: ActivityNotFoundException) {
              appComponent?.let { it1 -> meetingNotFoundDialog.showInvalidConferenceAlert(it1) }
              logger.error(TAG, LogEvent.EXCEPTION, LogEventValue.FURLLINKHANDLER, e.message.toString(), e)
            }
          }
        }
      }
    }
  }

  fun getSuggestResults(searchText: String) {
    profileManager.getSuggestResults(searchText, map, noProfileImageMap)
  }

  fun registerViewModelListener() {
    profileManager.registerViewModelListener()
  }

  // Restores the scroll state of the recycleview
  fun restoreScrollState() {
    if (meetingsFragment.rv_meetings_agenda != null) {
      val linearlayoutManager: LinearLayoutManager = meetingsFragment.rv_meetings_agenda.layoutManager as LinearLayoutManager
      linearlayoutManager.scrollToPositionWithOffset(lastFirstVisiblePosition, 0);
    }
  }

  // Saves the scroll state of the recycleview
  fun saveScrollPosition() {
    if (meetingsFragment.rv_meetings_agenda != null) {
      val linearlayoutManager: LinearLayoutManager = meetingsFragment.rv_meetings_agenda.layoutManager as LinearLayoutManager
      lastFirstVisiblePosition = linearlayoutManager.findFirstCompletelyVisibleItemPosition();
    }
  }

  // Sets the fragment to use for getting the recycleview instance
  fun setAgendaFragment(meetingsFragment: MeetingsFragment) {
    this.meetingsFragment = meetingsFragment
  }


  public override fun onCleared() {
    agendaDataTimer?.cancel()
    unRegisterMsalTokenChannel()
    compositeDisposable.clear()
    super.onCleared()
  }

  fun getMeetingRoomInfo(isUserInitiated : Boolean = false) {
    mRoomId =  mSharedPreferencesManager.prefDefaultMeetingRoom.toString()
    mRoomId.let {
      if (!mIsLoading && mMeetingRoomInfo == null && it.isNotEmpty()) {
        mIsLoading = true
        val getMeetingInfoDisposable = gmWebServiceRepository.getMeetingInformation(mRoomId.toInt())
                .subscribe({ meetingInfoResultResponse ->
                  mIsLoading = false
                  this.mMeetingRoomInfo = meetingInfoResultResponse
                  // If user tapped on Schedule Meeting menu item, open calendar intent.
                  if (isUserInitiated) {
                    scheduleMeeting(mMeetingRoomInfo)
                  }
                }, { error ->
                  mIsLoading = false
                  logger.error(TAG, LogEvent.EXCEPTION, LogEventValue.HOME, error.message.toString(), error)
                  // If user tapped on Schedule Meeting menu item and API failed, show unexpected error toast. Reusing existing String.xml element
                  if (isUserInitiated) {
                    appComponent?.let { context ->
                      val activity = context as Activity
                      activity.runOnUiThread {
                        Toast.makeText(context, R.string.failed_to_get_meeting_events_head, Toast.LENGTH_SHORT).show()
                      }
                    }
                  }
                })
        compositeDisposable.add(getMeetingInfoDisposable)
      }
    }
  }

  fun shareUrlAndDismissDialog() {
    shareMeetingURL()
    dismissDialog()
  }

  fun shareMeetingURL() {
    val sendIntent: Intent = Intent().apply {
      action = ACTION_SEND
      type = AppConstants.SHARE_TEXT_PLAIN
      val meetingUrl = String.format(appComponent?.getString(R.string.please_join_the_video_meeting_at) + AppConstants.COLON + AppConstants.BLANK_SPACE + joinUrl.value)
      val dialInNumber = String.format(appComponent?.getString(R.string.or_dial_in_with_this_number) + AppConstants.COLON) + AppConstants.BLANK_SPACE + mMeetingRoomInfo?.meetingRoomGetResult?.audioDetail?.primaryAccessNumber
      val guestPasscode = String.format(appComponent?.getString(R.string.guest_passcode_share_sheet) + AppConstants.COLON) + AppConstants.BLANK_SPACE + mMeetingRoomInfo?.meetingRoomGetResult?.audioDetail?.participantPassCode
      val title = mMeetingRoomInfo?.meetingRoomGetResult?.meetingRoomDetail?.conferenceTitle
      var shareMessage: String?
      shareMessage = if(!mMeetingRoomInfo?.meetingRoomGetResult?.audioDetail?.primaryAccessNumber.isNullOrEmpty() && !mMeetingRoomInfo?.meetingRoomGetResult?.audioDetail?.participantPassCode.isNullOrEmpty()) {
        meetingUrl + AppConstants.NEW_LINE_CHARACTER + AppConstants.NEW_LINE_CHARACTER + dialInNumber + AppConstants.NEW_LINE_CHARACTER + guestPasscode
      } else {
        meetingUrl
      }
      val subject = appComponent?.getString(R.string.invitation_to)?.let { String.format(it, AppConstants.BLANK_SPACE + mMeetingRoomInfo?.meetingRoomGetResult?.meetingRoomDetail?.conferenceTitle) }
      putExtra(EXTRA_SUBJECT, subject)
      putExtra(EXTRA_TITLE, title)
      putExtra(EXTRA_TEXT, shareMessage)
    }
    appComponent?.let {
      val pi = PendingIntent.getBroadcast(appComponent, AppConstants.ZERO,
              Intent(appComponent, ShareSheetBroadcastReceiverInOutMeeting::class.java).putExtra(AppConstants.MEETING,AppConstants.OUT_MEETING_SHARE),
              PendingIntent.FLAG_UPDATE_CURRENT)
      val shareIntent = createChooser(sendIntent, null, pi.intentSender)
      startActivity(it, shareIntent, null)
    }
  }
}