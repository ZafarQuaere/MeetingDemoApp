package com.pgi.convergencemeetings.meeting.gm5.ui.view

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.pgi.convergencemeetings.R

class MeetingSecurityOptionView(context: Context, attrs: AttributeSet): LinearLayout(context, attrs) {

    @BindView(R.id.tv_security_option_title)
    lateinit var tvTitle: TextView

    @BindView(R.id.tv_security_option_description)
    lateinit var tvDescription: TextView

    @BindView(R.id.sw_security_option)
    lateinit var swSecurityOption: Switch

    init {
        val view = inflate(context, R.layout.meeting_security_option_view, this)
        ButterKnife.bind(this, view)

        val attributes = context.obtainStyledAttributes(attrs, R.styleable.MeetingSecurityOptionView)
        tvTitle.text = attributes.getString(R.styleable.MeetingSecurityOptionView_security_option_title)
        tvDescription.text = attributes.getString(R.styleable.MeetingSecurityOptionView_security_option_description)
        attributes.recycle()
    }
}