package com.pgi.convergence.home.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.pgi.convergence.home.R
import com.pgi.convergence.home.databinding.UcchomecardcontainerBinding
import com.pgi.convergence.ui.UILifeCycleScope
import kotlinx.android.synthetic.main.ucchomecardcontainer.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/**
 * Created by Sudheer Chilumula on 2018-11-07.
 * PGi
 * sudheer.chilumula@pgi.com
 */
@ExperimentalCoroutinesApi
class HomeCardFragment : androidx.fragment.app.Fragment() {
  val mCardsViewModel: HomeCardsViewModel by sharedViewModel()
  internal var binding: UcchomecardcontainerBinding? = null
  var loginName: String? = null
    set(value) {
      field = value
      mCardsViewModel.name.postValue(value)
    }
  private val uiScope = UILifeCycleScope()
  private var firstlaunch: Boolean = true
  private val TAG = HomeCardFragment::class.java.simpleName

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    lifecycle.addObserver(uiScope)
    activity?.let { it ->
      mCardsViewModel.appComponent = it
      mCardsViewModel.name.postValue(loginName)
      mCardsViewModel.setHomeFragment(this)
    }
    mCardsViewModel.registerMsalTokenChannel()
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    binding = DataBindingUtil.inflate(inflater, R.layout.ucchomecardcontainer, container, false)
    binding?.lifecycleOwner = this
    binding?.cardsViewModel = mCardsViewModel
    mCardsViewModel.profileManager.recyclerView = binding?.homeCardsContainer
    mCardsViewModel.profileManager.lifecycleOwner = this
    mCardsViewModel.profileManager.activity = activity
    mCardsViewModel.registerViewModelListener()
    return binding?.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    activity.let {
      homeCardsContainer?.setHasFixedSize(true)
      homeCardsContainer?.itemAnimator = DefaultItemAnimator()
      val itemTouchHelper = cardSwipeHelper()
      itemTouchHelper.attachToRecyclerView(homeCardsContainer)
    }
  }

  override fun onResume() {
    super.onResume()
    if(firstlaunch) {
      firstlaunch = false
       mCardsViewModel.startLoading()
    }
    mCardsViewModel.triggerCardsDataTimer()
    mCardsViewModel.name.postValue(loginName)
    mCardsViewModel.registerViewModelListener()
  }

  override fun onPause() {
    super.onPause()
    mCardsViewModel.cancelCardDataTimer()
  }

  fun removeItem(position: Int) {
    mCardsViewModel.removeCard(position)
  }

    private fun cardSwipeHelper(): ItemTouchHelper {
    return ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
      0, ItemTouchHelper.RIGHT
    ) {
      override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: ViewHolder,
        target: ViewHolder
      ): Boolean {
        return false
      }

      override fun onSwiped(viewHolder: ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition
        removeItem(position)
      }
    })
  }

  fun handleO365Redirect(requestCode: Int, resultCode: Int, data: Intent?) {
    mCardsViewModel.handleInteractiveRequestRedirect(requestCode, resultCode, data)
  }
}