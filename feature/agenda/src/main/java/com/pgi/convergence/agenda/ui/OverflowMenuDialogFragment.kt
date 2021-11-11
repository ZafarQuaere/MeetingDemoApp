package com.pgi.convergence.agenda.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pgi.convergence.agenda.R
import com.pgi.convergence.agenda.databinding.OverflowMenuBottomSheetAgendaBinding
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
class OverflowMenuDialogFragment : BottomSheetDialogFragment(){

    private val TAG = OverflowMenuDialogFragment::class.java.name
    var binding: OverflowMenuBottomSheetAgendaBinding? = null
    val viewModel: MeetingsAgendaViewModel by sharedViewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.overflow_menu_bottom_sheet_agenda, container, false)
        binding?.let {
            it.user = viewModel
            it.user?.populateRoomData()
            it.user?.setDialogFragment(this)
        }
        return binding?.root
    }
}