package com.pgi.convergencemeetings.leftnav

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.pgi.convergence.utils.CommonUtils
import com.pgi.convergencemeetings.R
import com.pgi.convergencemeetings.base.ui.BaseActivity
import com.pgi.convergencemeetings.databinding.NavigationDrawerActivityBinding
import com.pgi.convergencemeetings.utils.AppAuthUtils
import com.pgi.convergencemeetings.utils.ClientInfoDaoUtils
import com.squareup.picasso.Picasso

class NavDrawerActivity : BaseActivity() {

  private var binding: NavigationDrawerActivityBinding? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val appUtils = AppAuthUtils.getInstance()
    val firstName = appUtils.firstName
    val lastName = appUtils.lastName
    val fullName = CommonUtils.getFullName(firstName, lastName)
    binding = DataBindingUtil.setContentView(this, R.layout.navigation_drawer_activity)
    binding?.backArrowBtn?.setOnClickListener(View.OnClickListener {
      this.finish()
    })
    binding?.profilePic = ClientInfoDaoUtils.getInstance().profileImageUrl
    Picasso.get()
        .load(binding?.profilePic)
        .fit()
        .into(binding?.drawerAvatar)
    binding?.initials = CommonUtils.getNameInitials(appUtils.firstName,
        appUtils.lastName)
    binding?.fullName = fullName
    binding?.email = AppAuthUtils.getInstance().emailId
  }
}