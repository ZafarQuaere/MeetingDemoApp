package com.pgi.convergencemeetings.meeting.gm5.di

import com.pgi.convergencemeetings.meeting.gm5.ui.MeetingEventViewModel
import com.pgi.convergencemeetings.meeting.gm5.ui.MeetingUserViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val meetingSecurityFragmentModule = module {

    val meetingEventViewModel = MeetingEventViewModel()
    val meetingUserViewModel = MeetingUserViewModel()

    viewModel { meetingEventViewModel }
    viewModel { meetingUserViewModel }
}