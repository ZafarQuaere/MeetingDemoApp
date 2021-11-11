package com.pgi.convergencemeetings.meeting.gm5.ui.view

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.pgi.convergencemeetings.R

class MeetingSecurityHeader(context: Context, attrs: AttributeSet? = null): LinearLayout(context, attrs) {

    @BindView(R.id.lock_room_view)
    lateinit var lockRoomView: MeetingSecurityOptionView

    @BindView(R.id.waiting_room_view)
    lateinit var waitingRoomView: MeetingSecurityOptionView

    @BindView(R.id.restrict_sharing_view)
    lateinit var restrictSharingView: MeetingSecurityOptionView

    @BindView(R.id.rl_waiting_room_guest_fragment)
    lateinit var rlWaitingRoomGuestFragment: RelativeLayout

    @BindView(R.id.waiting_room_guest_list_count)
    lateinit var tvWaitingRoomGuestListCount: TextView

    @BindView(R.id.waiting_room_guest_list_header)
    lateinit var tvWaitingRoomGuestListHeader: TextView

    @BindView(R.id.tv_admit_all)
    lateinit var tvAdmitAll: TextView

    init {
        val view = inflate(context, R.layout.meeting_security_header, this)
        ButterKnife.bind(this, view)
    }
}