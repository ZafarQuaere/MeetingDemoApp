package com.pgi.convergence.agenda.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.convergence.BaseFragment
import com.google.android.material.snackbar.Snackbar
import com.pgi.convergence.agenda.R
import com.pgi.convergence.agenda.databinding.FragmentMeetingsBinding
import com.pgi.convergence.constants.AppConstants
import com.pgi.convergence.ui.UILifeCycleScope
import com.pgi.convergence.utils.CommonUtils
import kotlinx.android.synthetic.main.fragment_meetings.*
import kotlinx.android.synthetic.main.fragment_meetings.view.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.serialization.UnstableDefault
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


@UnstableDefault
@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
class MeetingsFragment : BaseFragment(){

    private val TAG = MeetingsFragment::class.java.simpleName
    var binding: FragmentMeetingsBinding? = null
    val viewModel: MeetingsAgendaViewModel by sharedViewModel()
    private lateinit var linearLayoutManager: LinearLayoutManager
    private val uiScope = UILifeCycleScope()
    private var firstlaunch: Boolean = true
    var endMeetingThankyouSnakbar: Snackbar? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(uiScope)
        activity?.let { it ->
            viewModel.appComponent = it
        }
        viewModel.registerMsalTokenChannel()
        viewModel.setAgendaFragment(this)
        viewModel.getMeetingRoomInfo()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_meetings, container, false)
        binding?.lifecycleOwner = this
        linearLayoutManager = LinearLayoutManager(activity)
        binding?.let{
            it.user = viewModel
            if(viewModel.sharedPref.isGuestUser) {
                it.rlMeetingsHeader.visibility = GONE
            }
            it.tvMeetingUrl.setOnLongClickListener {
                CommonUtils.copyRoomURL(context, it.tv_meeting_url.text.toString(), AppConstants.COPY_URL_UNIVERSAL)
                true
            }
            it.rvMeetingsAgenda.layoutManager = linearLayoutManager as RecyclerView.LayoutManager?
            it.rvMeetingsAgenda.adapter = it.user?.getMeetingsAgendaAdapter()
            it.rvMeetingsAgenda.setHasFixedSize(true)
            it.user?.populateRoomData()
            it.user?.loadProfileImageOrText(it.ivAvatar)
            it.user?.expandTouchArea(it.rlMeetingsHeader, it.tvMeetingUrl, 100)
        }
        viewModel.profileManager.recyclerView = binding?.rvMeetingsAgenda
        viewModel.profileManager.lifecycleOwner = this
        viewModel.profileManager.activity = activity
        viewModel.registerViewModelListener()
        return binding?.root
    }

    override fun onResume() {
        super.onResume()
        if(firstlaunch) {
            firstlaunch = false
            viewModel.startLoading()
        }
        viewModel.getMeetingRoomInfo()
        viewModel.triggerAgendaDataTimer()
        viewModel.populateRoomData()
        viewModel.loadProfileImageOrText(iv_avatar)
        showEndMeetingThankYouToast()
        binding?.executePendingBindings()
    }

    override fun onPause() {
        super.onPause()
        viewModel.cancelCardDataTimer()
    }

    fun handleO365Redirect(requestCode: Int, resultCode: Int, data: Intent?) {
        viewModel.handleInteractiveRequestRedirect(requestCode, resultCode, data)
    }

// Showing toast for end meeting thankyou
    fun showEndMeetingThankYouToast() {
        if (CommonUtils.isEndMeetingThankYouSnackBarVisible()) {
            endMeetingThankyouSnakbar = binding?.floatingSnackBar?.let {
                Snackbar.make(it, R.string.thank_you_for_feedback, Snackbar.LENGTH_SHORT)
                        .setActionTextColor(ContextCompat.getColor(context!!, R.color.white))
            }
            if (endMeetingThankyouSnakbar?.isShownOrQueued == false) {
                endMeetingThankyouSnakbar?.show()
                CommonUtils.showEndMeetingThankYouSnackBar(false)
            }
        }
    }
}