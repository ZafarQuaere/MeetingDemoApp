package com.pgi.convergencemeetings.leftnav.settings.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.pgi.convergence.constants.AppConstants
import com.pgi.convergence.persistance.SharedPreferencesManager
import com.pgi.convergence.utils.CommonUtils
import com.pgi.convergencemeetings.R
import com.pgi.convergencemeetings.meeting.gm4.ui.DefaultRoomActivity
import com.pgi.convergencemeetings.base.ui.BaseActivity
import com.pgi.convergencemeetings.models.Setting
import com.pgi.convergencemeetings.utils.ClientInfoDaoUtils
import com.pgi.logging.enums.Interactions
import java.util.*

/**
 * This DialInActivityContract will enable users to change account setting like default room, reset password and change username
 */
class SettingActivity : BaseActivity(), SettingItemClickListener {
  @BindView(R.id.rv_setting_recycler_view)
	lateinit var rvSettingView: RecyclerView

  @BindView(R.id.tv_client_id)
	lateinit var tvClientId: TextView
	private var mClientInfoDoaUtils = ClientInfoDaoUtils.getInstance()
	private var mClientId: String? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setInterActionName(Interactions.SETTINGS.interaction)
		setContentView(R.layout.setting_activity)
		initViews()
	}

	private fun initViews() {
		ButterKnife.bind(this)
	}

	override fun onStart() {
		super.onStart()
		mClientInfoDoaUtils.refereshClientInfoResultDao()
		mClientId = mClientInfoDoaUtils.clientId
		initSettingItems()
	}

	//Initilized the setting menu options.
	private fun initSettingItems() {
		val navigationItems = getSettingsItems(this)
		val drawerAdapter = SettingListAdapter(navigationItems, this)
		rvSettingView.adapter = drawerAdapter
		rvSettingView.layoutManager = LinearLayoutManager(this)
		val clientIdMsg = resources.getString(R.string.client_Id)
		mClientId = mClientInfoDoaUtils?.clientId
		if (mClientId != null) {
			tvClientId.text = clientIdMsg + AppConstants.BLANK_SPACE + mClientId
		} else tvClientId.visibility = View.INVISIBLE
	}

	override fun settingItemClicked(position: Int) {
		var intent: Intent? = null
		when (position) {
			AppConstants.SETTING_DEFAULT_ROOM -> intent = Intent(this, DefaultRoomActivity::class.java)
			AppConstants.SETTING_DISPLAY_NAME -> intent = Intent(this, UpdateNameActivity::class.java)
			AppConstants.SETTING_MEETING_SETTING -> {
			}
		}
		intent?.let { startActivity(it) }
	}

	/**
	 * On setting close.
	 */
	@OnClick(R.id.iv_setting_close)
	fun onSettingClose() {
		finish()
	}

	companion object {
		fun getSettingsItems(context: Context): List<Setting> {
			val settingItems: MutableList<Setting> = ArrayList(3)
			val clientInfoDaoUtils = ClientInfoDaoUtils.getInstance()
			var fullName = clientInfoDaoUtils.fullName
			var defaultMeetingRoom: String? = null
			val defaultMeetingRoomId = SharedPreferencesManager.getInstance().prefDefaultMeetingRoom
			val meetingRoomList = clientInfoDaoUtils.meetingRooms
			if (meetingRoomList != null && !meetingRoomList.isEmpty()) {
				for (meetingRoom in meetingRoomList) {
					if (meetingRoom != null && defaultMeetingRoomId.equals(meetingRoom.meetingRoomId, ignoreCase = true)) {
						defaultMeetingRoom = AppConstants.SLASH_SYMBOL + meetingRoom.roomName
						break
					} else {
						defaultMeetingRoom = AppConstants.EMPTY_STRING
					}
				}
			} else {
				defaultMeetingRoom = AppConstants.EMPTY_STRING
			}
			if (CommonUtils.isUsersLocaleJapan()) {
				fullName = CommonUtils.formatJapaneseName(fullName)
			}
			val settingItem1 = Setting(R.drawable.ic_about_carat_btn, context.getString(R.string.default_meeting_url), true, defaultMeetingRoom)
			val settingItem2 = Setting(R.drawable.ic_about_carat_btn, context.getString(R.string.display_name_in_meeting), true, fullName)
			//Setting SettingItem3 = new Setting(R.drawable.ic_about_carat_btn, context.getString(R.string.meeting_setting), false, null);
			settingItems.add(settingItem1)
			settingItems.add(settingItem2)
			//settingItems.add(SettingItem3);
			return settingItems
		}
	}
}