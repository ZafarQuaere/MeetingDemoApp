package com.pgi.convergencemeetings.meeting.gm5.ui.chat

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.VisibleForTesting
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.pgi.convergence.application.CoreApplication
import com.pgi.convergence.constants.AppConstants
import com.pgi.convergencemeetings.R
import com.pgi.convergencemeetings.meeting.gm5.data.model.User
import com.pgi.convergencemeetings.meeting.gm5.ui.MeetingEventViewModel
import com.pgi.convergencemeetings.meeting.gm5.ui.MeetingUserViewModel
import com.pgi.convergencemeetings.meeting.gm5.ui.WebMeetingActivity
import com.pgi.convergencemeetings.meeting.gm5.ui.particiapnts.ParticipantsOrder
import com.pgi.convergencemeetings.meeting.gm5.ui.particiapnts.WebParticipantListAdapter
import com.pgi.convergencemeetings.models.Chat
import com.pgi.logging.Logger
import com.pgi.logging.enums.LogEvent
import com.pgi.logging.enums.LogEventValue
import java.util.ArrayList

class NewPrivateChatSelectionFragment : Fragment() {

    private val TAG = NewPrivateChatSelectionFragment::class.java.simpleName

    @BindView(R.id.rvChatParticipantList)
    lateinit var rvChatParticipantList: RecyclerView

    var mWebParticipantListAdapter: WebParticipantListAdapter? = null
    var mUserList: MutableList<User> = ArrayList()

    var mMeetingEventsModel: MeetingEventViewModel? = null
        private set
    var mMeetingUserViewModel: MeetingUserViewModel? = null

    var addOrRemoveFragment: ((Boolean) -> Unit)? = null

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var isPrivateChatEnable = true

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val mLogger: Logger = CoreApplication.mLogger

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    lateinit var mContext: Context

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    lateinit var parentActivity : WebMeetingActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        initializeParentActivity(activity)
    }

    fun initializeParentActivity(activity: FragmentActivity?) {
        if (activity is WebMeetingActivity) {
            parentActivity = activity
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.let {
            mMeetingEventsModel =  ViewModelProviders.of(it).get(MeetingEventViewModel::class.java)
            mMeetingUserViewModel =  ViewModelProviders.of(it).get(MeetingUserViewModel::class.java)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_select_chat_user, container, false)
        ButterKnife.bind(this, view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val layoutManager = LinearLayoutManager(context)
        rvChatParticipantList.layoutManager = layoutManager
        registerVmListener()
    }

    private fun registerVmListener() {
        mMeetingEventsModel?.privateChatEnableStatus?.observe(this@NewPrivateChatSelectionFragment, Observer { updatePrivateChatStatus(it) })
        mMeetingEventsModel?.users?.observe(this, Observer { userList -> updateUserList(userList) })
    }

    fun updatePrivateChatStatus(privateChatStatus: Boolean) {
       isPrivateChatEnable = privateChatStatus
    }

    fun updateUserList(userList: List<User>) {
        // clearing the existing list
        mUserList.clear()
        for (user in userList) {
            if (user.isSelf || user.initials.equals(AppConstants.POUND_SYMBOL) || ParticipantsOrder.checkUserforWebpresence(user)) { // self user and audio only user is filtered
                continue
            }
            mUserList.add(user)
        }
        val orderedList = ParticipantsOrder.getSortedList(mUserList)
        mWebParticipantListAdapter = mMeetingUserViewModel?.let { context?.let { it1 -> WebParticipantListAdapter(it, it1, orderedList,true) } }
        mWebParticipantListAdapter?.onShowChatDetailPage = ::onShowChatDetailPage
        rvChatParticipantList.adapter = mWebParticipantListAdapter
    }

    fun onShowChatDetailPage(user : User) {
        if (isPrivateChatEnable) {
            val chatItem = Chat()
            if (!user.name.isNullOrEmpty()) {
                chatItem.firstName = user.firstName.toString()
                chatItem.lastName = user.lastName.toString()
            }
            chatItem.profileImage = user.profileImage
            chatItem.initials = user.initials
            chatItem.webPartId = user.id.toString()
            mMeetingUserViewModel?.setUserRole(user, requireContext())
            mMeetingUserViewModel?.logMixPanelEventForNewPrivateChat(AppConstants.PRIVATE_CHAT,mMeetingUserViewModel?.userType,
                AppConstants.PRIVATE_CHAT_FROM_CHAT)
            // call the method to display ChatDetails fragment
            parentActivity.onShowChatDetailPage(chatItem)
        } else {
            parentActivity.showDisablePrivateChatToast(isPrivateChatEnable)
        }
    }

    fun removeSelectPrivateChatFragment() {
        // remove SelectPrivateChatFragment
        addOrRemoveFragment?.invoke(false)
    }
}