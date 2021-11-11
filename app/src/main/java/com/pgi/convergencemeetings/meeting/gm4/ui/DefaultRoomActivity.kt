package com.pgi.convergencemeetings.meeting.gm4.ui

import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.newrelic.agent.android.NewRelic
import com.pgi.convergence.persistance.SharedPreferencesManager
import com.pgi.convergence.utils.CommonUtils
import com.pgi.convergence.utils.ElkTransactionIDUtils
import com.pgi.convergencemeetings.R
import com.pgi.convergencemeetings.base.ui.BaseActivity
import com.pgi.convergencemeetings.models.DefaultRoom
import com.pgi.convergencemeetings.utils.ClientInfoDaoUtils
import com.pgi.logging.enums.LogEvent
import com.pgi.logging.enums.LogEventValue
import java.net.MalformedURLException
import java.net.URL
import java.util.*

/**
 * Created by ashwanikumar on 12/14/2017.
 */
class DefaultRoomActivity : BaseActivity(), DefaultRoomItemClickListener {

	private val TAG = DefaultRoomActivity::class.java.simpleName

	var mDefaultRooms: MutableList<DefaultRoom> = ArrayList(10)

  @BindView(R.id.rv_default_room)
	lateinit var rvDefaultView: RecyclerView

	private var mSharedPreferenceManager = SharedPreferencesManager.getInstance()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		NewRelic.setInteractionName("Default Meeting View")
		setContentView(R.layout.default_meeting_room)
		initViews()
	}

	private fun initViews() {
		ButterKnife.bind(this)
		populateDefaultRoomData()
		initDefaultRoomAdapter()
	}

	private fun initDefaultRoomAdapter() {
		val drawerAdapter = DefaultRoomAdapter(mDefaultRooms, this)
		rvDefaultView.adapter = drawerAdapter
		rvDefaultView.layoutManager = LinearLayoutManager(this)
	}

	private fun populateDefaultRoomData() {
		val defaultMeetingRoomId = defaultMeetingRoom
		val meetingRoomList = ClientInfoDaoUtils.getInstance().meetingRooms
		if (meetingRoomList.isNotEmpty()) {
			for (meetingRoom in meetingRoomList) {
				val meetingRoomUrls = meetingRoom.meetingRoomUrls
				var joinUrl: String? = null
				if (meetingRoomUrls != null) {
					joinUrl = meetingRoomUrls.attendeeJoinUrl
				}
				if (joinUrl != null) {
					var aURL: URL
					var roomName: String?
					try {
						aURL = URL(joinUrl)
						roomName = aURL.path
						joinUrl = aURL.host
						var isDefaultMeetingRoom = false
						if (defaultMeetingRoomId.equals(meetingRoom.meetingRoomId, ignoreCase = true)) {
							isDefaultMeetingRoom = true
						}
						val meetingRoomId = meetingRoom.meetingRoomId
						val defaultRoom = DefaultRoom(joinUrl, roomName, isDefaultMeetingRoom, meetingRoomId)
						mDefaultRooms.add(defaultRoom)
					} catch (e: MalformedURLException) {
						Log.e(TAG, e.message)
					}
				} else {
					Log.d(TAG, this.getString(R.string.empty_join_url_error))
				}
			}
		}
	}

	private val defaultMeetingRoom: String
		get() = CommonUtils.getDefaultMeetingRoom(this)

	override fun defaultRoomSelected(meetingRoomId: String?) {
		mlogger.info(TAG, LogEvent.APP_VIEW, LogEventValue.SETTINGS, "DefaultRoomActivity - " +
				"Switched rooms", null, null, true, false)
		ElkTransactionIDUtils.resetTransactionId()
		val defaultMeetingRoomId = defaultMeetingRoom
		if (defaultMeetingRoomId != meetingRoomId) {
			mSharedPreferenceManager.prefDefaultMeetingRoom = meetingRoomId
		}
	}

	private fun setDefaultMeetingRoom() {
		val clientInfoDaoUtils = ClientInfoDaoUtils.getInstance()
		clientInfoDaoUtils.refereshClientInfoResultDao()
		val meetingRoomList = clientInfoDaoUtils.meetingRooms
		if (meetingRoomList != null && !meetingRoomList.isEmpty()) {
			val meetingRoom = meetingRoomList[0]
			if (meetingRoom != null) {
				val meetingRoomId = meetingRoom.meetingRoomId
				if (meetingRoomId != null) {
					mSharedPreferenceManager.prefDefaultMeetingRoom = meetingRoomId
				}
			}
		}
	}

	@OnClick(R.id.btn_close)
	fun onBackBtnClick() {
		finish()
	}
}