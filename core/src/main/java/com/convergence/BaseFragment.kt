package com.convergence

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.convergence.core.R

open class BaseFragment : Fragment(){

    @SuppressLint("CheckResult")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val content = Button(activity)
        content.setBackgroundColor(activity!!.resources.getColor(R.color.bg_home))
        return content
    }

}