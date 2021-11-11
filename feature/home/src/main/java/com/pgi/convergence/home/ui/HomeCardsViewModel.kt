package com.pgi.convergence.home.ui

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.pgi.convergence.common.profile.ProfileManager
import com.pgi.convergence.data.enums.home.HomeCardType
import com.pgi.convergence.data.enums.home.HomeCardType.*
import com.pgi.convergence.data.enums.msal.MsalMeetingRoom.GMROOM
import com.pgi.convergence.data.enums.msal.MsalMeetingStatus
import com.pgi.convergence.data.model.home.CardProfile
import com.pgi.convergence.data.model.home.HomeCardData
import com.pgi.convergence.data.repository.msal.MSALAuthRespositoryImpl
import com.pgi.convergence.data.repository.msal.MSALGraphRepositoryImpl
import com.pgi.convergence.dialogs.MeetingNotFoundDialog
import com.pgi.convergence.enums.JoinMeetingEntryPoint
import com.pgi.convergence.home.BuildConfig
import com.pgi.convergence.home.R
import com.pgi.convergence.home.R.style
import com.pgi.convergence.persistance.SharedPreferencesManager
import com.pgi.convergence.ui.SharedViewModel
import com.pgi.convergence.utils.FeaturesUtil
import com.pgi.logging.Logger
import com.pgi.logging.enums.LogEvent
import com.pgi.logging.enums.LogEvent.EXCEPTION
import com.pgi.logging.enums.LogEventValue
import com.pgi.logging.enums.LogEventValue.*
import com.pgi.logging.enums.Mixpanel
import com.pgi.network.models.SearchResult
import com.pgi.network.viewmodels.ViewModelResource
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.ucchomecardcontainer.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.serialization.UnstableDefault
import org.joda.time.LocalDate
import java.util.*
import java.util.Collections.emptyList
import kotlin.concurrent.fixedRateTimer


/**
 * This is the ViewModel for HomeCardFragment.
 * All the data needed for the HomeCardFragment will be provided by this model
 *
 * @author Sudheer R Chilumula
 */
@UnstableDefault
@ExperimentalCoroutinesApi
class HomeCardsViewModel(
    val authRepository: MSALAuthRespositoryImpl,
    val graphRepository: MSALGraphRepositoryImpl,
    val sharedViewModel: SharedViewModel,
    val sharedPref: SharedPreferencesManager,
    val profileManager: ProfileManager,
    val logger: Logger
) : ViewModel() {

    private val tag = HomeCardsViewModel::class.java.simpleName
    private val homeCardAdapter: HomeCardAdapter = HomeCardAdapter(R.layout.ucchomecard, this)
    private val mMeetingCardTransitionIntervalInMin: Int = 15
    private val mCardRefreshInterval = 30000L
    private val maxResults = 50
    private val msalViewModel by lazy { sharedViewModel.msalModel }
    private val meetingNotFoundDialog = MeetingNotFoundDialog()
    private var cardsDataTimer: Timer? = null
    private var coroutineExceptionHandler: (LogEventValue, String) -> CoroutineExceptionHandler =
        { logEventValue: LogEventValue, method: String ->
            CoroutineExceptionHandler { _, exception ->
                GlobalScope.launch {
                    logger.error(tag, EXCEPTION, logEventValue, "HomeCardsViewModel: $method", exception)
                }
            }
        }
    private val cardDataList: MutableLiveData<List<HomeCardData>> by lazy { MutableLiveData<List<HomeCardData>>() }
    val name: MutableLiveData<String> by lazy { MutableLiveData<String>(" ") }
    val barBG: MutableLiveData<Int> by lazy {MutableLiveData<Int>(Color.parseColor("#00aeef"))}
    private var authInProgress: Boolean = false
    val loadingData: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>(false) }
    val cardsData: LiveData<List<HomeCardData>> = cardDataList
    val loginName: LiveData<String> = name
    val topbarBg: LiveData<Int> = barBG
    val launchUrl: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val entryPoint: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val isLoading: LiveData<Boolean> = loadingData
    val map: MutableMap<String, String> = mutableMapOf()
    var office365Token: String? = null
    var mCardsList = emptyList<HomeCardData>()
        private set
    var updatedCards = emptyList<HomeCardData>()
    var greetingMsg: String = getGreeting()
    private val compositeDisposable = CompositeDisposable()
    val mSearchResponse: MutableLiveData<ViewModelResource<List<SearchResult>>> = MutableLiveData()
    val noProfileImageMap: MutableMap<String, String> = mutableMapOf()
    var newCards= emptyList<HomeCardData>()
    var homeCardFragment = HomeCardFragment()
    var lastFirstVisiblePosition: Int = 0
    var appComponent: Context? = null
        set(value) {
            field = value
            msalViewModel.context = value
            msalViewModel.packagaName = value?.packageName
            sharedViewModel.state.context = value
            sharedViewModel.state.packageName = value?.packageName
            authRepository.mActivity = value
        }
    var flavor = BuildConfig.FLAVOR

    fun getHomeCardAdapter(): HomeCardAdapter {
        return homeCardAdapter
    }

    fun checkIfMsalUserisAutheticated(): Boolean {
        return authRepository.getUserCount() != 0
    }

    fun authenticateMsalUser() {
        viewModelScope.launch(
            Dispatchers.Main + coroutineExceptionHandler(
                MSAL_AUTH,
                "authenticateMsalUser()"
            )
        )
        {
            startLoading()
            if (!authInProgress) {
                authInProgress = true
                authRepository.authenticateUser()
            }
        }
    }

    @UseExperimental(ObsoleteCoroutinesApi::class)
    fun registerMsalTokenChannel() {
        viewModelScope.launch(
            Dispatchers.Main + coroutineExceptionHandler(
                MSAL_AUTH,
                "registerMsalTokenChannel()"
            )
        ) {
            authRepository.msalAccessTokenChannel.consumeEach {
                office365Token = it
                authInProgress = false
                if (office365Token != null) {
                    if (sharedPref.isfirstTimeMsalUser()) {
                        sharedPref.firstTimeMsalUser(false)
                        triggerCardsDataTimer()
                    } else {
                        getCalendarData()
                    }
                } else {
                    sharedPref.firstTimeMsalUser(true)
                    onCardUpdateInterval()
                }
            }
        }
    }

    fun unRegisterMsalTokenChannel() {
        viewModelScope.launch(
            Dispatchers.Main + coroutineExceptionHandler(
                MSAL_AUTH,
                "unRegisterMsalTokenChannel()"
            )
        ) {
            authRepository.msalAccessTokenChannel.close()
        }
    }

    fun handleInteractiveRequestRedirect(requestCode: Int, resultCode: Int, data: Intent?) {
        removeAllCards()
        try {
            authRepository.handleInteractiveRequestRedirect(requestCode, resultCode, data)
        } catch (e:Exception) {
            logger.error(tag, EXCEPTION, HOME, e.message.toString(), e)
        }
    }

    fun startLoading() {
        getDummyData()
        loadingData.postValue(true)
    }

    fun triggerCardsDataTimer() {
        if (!authInProgress) {
            cardsDataTimer = fixedRateTimer(
                "updateCardData",
                false,
                0L,
                mCardRefreshInterval
            ) {
                onCardUpdateInterval()
            }
        }
    }

    fun cancelCardDataTimer() {
        cardsDataTimer?.cancel()
        cardsDataTimer?.purge()
        cardsDataTimer = null
    }

    private fun getFirstTimeUserWelcomeCardData() {
        mCardsList = msalViewModel.getFirstTimeUserCardData()
        sortCardsDataAndUpdate()
    }

    private fun getDummyData() {
        sortCardsDataAndUpdate()
        mCardsList = listOf(
            HomeCardData(
                "1",
                GUEST_JOIN_NOW_MEETINGS,
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
        sortCardsDataAndUpdate()
    }

    fun loadProfileImageHome(circleImageView: CircleImageView?, string: String?) {
        if (string != null) {
            Picasso.get()
                .load(string)
                .fit()
                .into(circleImageView)
        }
    }
    private fun onCardUpdateInterval() {
        greetingMsg = getGreeting()
        retreiveHomeData()
    }


    private fun retreiveHomeData() {
        if (sharedPref.isfirstTimeMsalUser()) {
            getFirstTimeUserWelcomeCardData()
            loadingData.postValue(false)
            cancelCardDataTimer()
        } else {
            getCalendarData()
        }
    }

    private fun getCalendarData() {
        viewModelScope.launch(
            Dispatchers.IO + coroutineExceptionHandler(
                MSAL_GETEVENTS,
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
                    msalViewModel.parseMsalCalEvent(events)
                    mCardsList = sharedViewModel.state.getHomeCards()
                    sortCardsDataAndUpdate()
                    loadingData.postValue(false)
                }
            }
        }
    }

    fun getCardAtIndex(position: Int): HomeCardData {
        return updatedCards[position]
    }

    fun removeCard(position: Int) {
        val card = getCardAtIndex(position)
        removeCardData(card, Mixpanel.DISMISS_SWIPE)
    }

    private fun removeCardData(card: HomeCardData, action: Mixpanel) {
        viewModelScope.launch(
            Dispatchers.IO + coroutineExceptionHandler(
                MSAL_GETEVENTS,
                "removeCard()"
            )
        ) {
            office365Token?.let { token ->
                val extensionData = msalViewModel.getDismissCardExtension(card.id)
                extensionData?.let {
                    it.dismissals += msalViewModel.mapCardTypeToState(card.type)
                    graphRepository.dismissCard(token, card.id, it)
                }
                mCardsList.find { it.id == card.id }?.dismissed = true
                sortCardsDataAndUpdate()
            }

            mCardsList.find { it.id == card.id }?.dismissed = true
            sortCardsDataAndUpdate()
            mixpaneDismissHomeCardEvent(action)
        }
    }

    fun removeAllCards() {
        mCardsList = emptyList()
        sortCardsDataAndUpdate()
    }

    fun sortCardsDataAndUpdate() {
        newCards = updatedCards
        updatedCards = emptyList()
        mCardsList = mCardsList.filter { !it.dismissed && !it.isAllDay }
        updatedCards = mCardsList.filter { it.type != UPCOMING_MEETINGS }
        val upcomingCards = mCardsList.filter { it.type == UPCOMING_MEETINGS }
        if (upcomingCards.size > 1) {
            for ((count, card) in upcomingCards.reversed().withIndex()) {
                if (count != 0) {
                    card.timeStatus = appComponent?.getString(R.string
                        .home_card_time_meetings_left, count + 1)
                }
            }
            updatedCards = updatedCards + upcomingCards[0]
        } else if (upcomingCards.size == 1) {
            updatedCards = updatedCards + upcomingCards
        }
        homeCardAdapter?.cardsData = updatedCards
        cardDataList.postValue(updatedCards)

        if(!sharedPref.isfirstTimeMsalUser()) {
            if(!newCards.isNullOrEmpty()){
                runOnUiThread(newCards)
            }
            else {
                runOnUiThread(updatedCards)
            }
        }
    }

    fun runOnUiThread(cardData: List<HomeCardData>) {
        homeCardFragment.activity?.runOnUiThread(java.lang.Runnable {
            saveScrollPosition()
            homeCardAdapter.notifyDataSetChanged()
            restoreScrollState()
        })
    }

    fun getGreeting(): String {
        val c = Calendar.getInstance()
        val timeOfDay = c.get(Calendar.HOUR_OF_DAY)

        return if (timeOfDay in 0..11) {
            barBG.postValue(Color.parseColor("#00aeef"))
            appComponent?.getString(R.string.greet_morning) ?:
            when(Locale.getDefault().getLanguage()) {
                "de" -> "Guten Morgen,"
                "fr"->  "Bonjour,"
                "ja"->  "おはようございます、"
                "nl"->  "Goedemorgen,"
                else -> "Good Morning,"

            }
        } else if (timeOfDay in 12..15) {
            barBG.postValue(Color.parseColor("#4997f3"))
            appComponent?.getString(R.string.greet_afternoon) ?:
            when(Locale.getDefault().getLanguage()) {
                "de" -> "Guten Tag,"
                "fr"->  "Bon après-midi,"
                "ja"->  "こんにちは、"
                "nl"->  "Goedemiddag,"
                else -> "Good Afternoon,"
            }
        } else {
            barBG.postValue(Color.parseColor("#625883"))
            appComponent?.getString(R.string.greet_evening) ?:
            when(Locale.getDefault().getLanguage()) {
                "de" -> "Guten Abend,"
                "fr"->  "Bonsoir,"
                "ja"->  "こんばんは、"
                "nl"->  "Goedenavond,"
                else -> "Good Evening,"
            }
        }
    }


    fun joinMeeting(type: HomeCardType, link: String?, profile: CardProfile?, entryPoint: JoinMeetingEntryPoint) {
        if (type == WELCOME_CARD) {
            cancelCardDataTimer()
            authenticateMsalUser()
            mCardsList = emptyList()
            sortCardsDataAndUpdate()
            mixpanelEnableIntegrationEvent()
        } else {
            if (!link.isNullOrEmpty() && profile != null && profile.subhead != null && profile.subhead!!.isUrl) {
                if (profile.subhead?.meetingRoom == GMROOM && FeaturesUtil.checkLaunchUrlDomainMatchesPackage(flavor, link)) {
                    this.entryPoint.postValue(entryPoint.getValue())
                    launchUrl.postValue(link)
                } else {
                    appComponent?.let {
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.data = Uri.parse(link)
                        try {
                            ContextCompat.startActivity(it, intent, null)

                        } catch (e: ActivityNotFoundException) {
                            appComponent?.let { it1 -> meetingNotFoundDialog.showInvalidConferenceAlert(it1) }
                            logger.error(tag, EXCEPTION, LogEventValue.FURLLINKHANDLER, e.message.toString(), e)
                        }
                    }
                }
            }
        }
    }

    fun getSuggestResults(searchText: String) {
        profileManager.getSuggestResults(searchText, map, noProfileImageMap)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun getDrawable(key: String): Drawable? {
        val resId = appComponent?.resources?.getIdentifier(key, "drawable", msalViewModel.packagaName)
        val drawable = resId?.let { appComponent?.resources?.getDrawable(it, appComponent?.theme) }
        return drawable
    }

    private val MIXPANEL_EVENT : String = "Mixpanel Event: "
    private fun mixpanelEnableIntegrationEvent() {
        val msg = MIXPANEL_EVENT + "Enable Integration"
        logger.info(tag, LogEvent.MIXPANEL_EVENT, LogEventValue.MIXPANEL_ENABLE_INTEGRATION, msg,
            null, null, false, true)
    }

    private fun mixpaneDismissHomeCardEvent(type : Mixpanel) {
        logger.mixpanelItem1 = type
        val msg = MIXPANEL_EVENT + "Dismiss Home Card from " + type.value
        logger.info(tag, LogEvent.MIXPANEL_EVENT, LogEventValue.MIXPANEL_DISMISS_HOME_CARD, msg,
            null, null, false, true)
    }

    fun showPopupMenu(view: View, cardData: HomeCardData) {
        appComponent?.let { it ->
            val popup = PopupMenu(it, view, GravityCompat.END, 0, style.CardPopupMenu)
            popup.menuInflater.inflate(R.menu.card_overflow_menu, popup.menu)
            popup.setOnMenuItemClickListener {
                removeCardData(cardData, Mixpanel.DISMISS_MENU)
                true
            }
            popup.show()
        }
    }

    fun registerViewModelListener() {
        profileManager.registerViewModelListener()
    }

    // Restores the scroll state of the recycleview
    fun restoreScrollState(){
        if(homeCardFragment.homeCardsContainer != null) {
            val linearlayoutManager: LinearLayoutManager = homeCardFragment.homeCardsContainer.layoutManager as LinearLayoutManager
            linearlayoutManager.scrollToPositionWithOffset(lastFirstVisiblePosition, 0);
        }
    }

    // Saves the scroll state of the recycleview
    fun saveScrollPosition() {
        if(homeCardFragment.homeCardsContainer != null) {
            val linearlayoutManager: LinearLayoutManager = homeCardFragment.homeCardsContainer.layoutManager as LinearLayoutManager
            lastFirstVisiblePosition = linearlayoutManager.findFirstCompletelyVisibleItemPosition();
        }
    }

    // Sets the fragment to use for getting the recycleview instance
    fun setHomeFragment(homeCardFragment: HomeCardFragment) {
        this.homeCardFragment = homeCardFragment
    }

// TODO:: Need to use this later when we implement signout
//  fun signOutMaslUser() {
//    viewModelScope.launch(
//      Dispatchers.IO + coroutineExceptionHandler(
//        MSAL_GETEVENTS,
//        "signOutMaslUser()"
//      )
//    ) {
//      removeAllCards()
//      cardsDataTimer?.cancel()
//      unRegisterMsalTokenChannel()
//      authRepository.signOutAllUsers()
//    }
//  }

    public override fun onCleared() {
        cardsDataTimer?.cancel()
        unRegisterMsalTokenChannel()
        compositeDisposable.clear()
        super.onCleared()
    }

}