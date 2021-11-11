package com.pgi.convergencemeetings.search.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.pgi.convergence.utils.CommonUtils
import com.pgi.convergencemeetings.R
import com.pgi.convergencemeetings.base.ui.BaseActivity
import com.pgi.convergencemeetings.databinding.SearchActivityBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.pgi.convergencemeetings.utils.AppAuthUtils
import com.pgi.convergencemeetings.meeting.gm5.ui.ViewModelResource


class SearchActivity: BaseActivity() {

  val joinFragmentViewModel: JoinFragmentViewModel by viewModel()
  private var binding: SearchActivityBinding? = null
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = DataBindingUtil.setContentView(this, R.layout.search_activity)
    if (savedInstanceState == null) {
      setupNavigation()
    }
  }

  private fun setupNavigation() {
    val navController = findNavController(R.id.search_nav_host)
    setSupportActionBar(binding?.searchToolbar)
    setupActionBarWithNavController(navController)
    binding?.searchBackBtn?.setOnClickListener(View.OnClickListener {
      CommonUtils.hideKeyboard(this)
      this.finish()
    })

    binding?.searchCloseBtn?.setOnClickListener(View.OnClickListener {
      joinFragmentViewModel.mSearchResponse.postValue(ViewModelResource.success(emptyList()))
      binding?.searchCloseBtn?.visibility = View.GONE
      binding?.searchEditText?.text?.clear()
      CommonUtils.hideKeyboard(this)
    })

    binding?.searchEditText?.addTextChangedListener(object: TextWatcher {
      override fun afterTextChanged(s: Editable) {

      }

      override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
      }

      override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if(s.isNotEmpty() && s.length >= 3){
          var doGMSearch = true
          if (AppAuthUtils.getInstance().isUserTypeGuest) {
            doGMSearch = false
          }
          joinFragmentViewModel.getSuggestResults(s.toString(), doGMSearch)
          binding?.searchCloseBtn?.visibility = View.VISIBLE
        } else if(s.isEmpty()){
          binding?.searchCloseBtn?.visibility = View.GONE
          joinFragmentViewModel.mSearchResponse.postValue(ViewModelResource.loading(emptyList()))
        } else {
          joinFragmentViewModel.getSuggestResults(s.toString(), false)
          binding?.searchCloseBtn?.visibility = View.VISIBLE
        }
      }
    })
  }

  override fun onResume() {
    super.onResume()
    binding?.searchCloseBtn?.visibility = View.GONE
    binding?.searchEditText?.text?.clear()
    CommonUtils.hideKeyboard(this)
  }

  override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
    super.onRestoreInstanceState(savedInstanceState)
    setupNavigation()
  }

  override fun onSupportNavigateUp(): Boolean {
    return findNavController(R.id.search_nav_host).navigateUp()
  }
}