package com.pgi.convergencemeetings.leftnav.about.ui

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.pgi.convergencemeetings.R

/**
 * Created by amit1829 on 11/14/2017.
 */
class AboutItemViewHolder(itemView: View, listener: View.OnClickListener?) : RecyclerView.ViewHolder(itemView) {
  @BindView(R.id.iv_about_carat_icon)
	lateinit var itemImage: ImageView

  @BindView(R.id.tv_list_label)
	lateinit var itemName: TextView

	init {
		ButterKnife.bind(this, itemView)
		itemView.setOnClickListener(listener)
	}
}