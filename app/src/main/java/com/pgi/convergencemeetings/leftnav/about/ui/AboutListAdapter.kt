package com.pgi.convergencemeetings.leftnav.about.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pgi.convergencemeetings.R
import com.pgi.convergencemeetings.leftnav.about.ui.AboutItemClickListener
import com.pgi.convergencemeetings.leftnav.about.ui.AboutItemViewHolder
import com.pgi.convergencemeetings.models.About

/**
 * Created by amit1829 on 11/14/2017.
 */
class AboutListAdapter(private val mAboutItems: List<About>,
											 private val mItemClickListener: AboutItemClickListener) :
		RecyclerView.Adapter<RecyclerView.ViewHolder>(), View.OnClickListener {
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
		val layout = LayoutInflater.from(parent.context).inflate(R.layout.about_list_item, parent, false)
		val itemHolder = AboutItemViewHolder(layout, this)
		layout.tag = itemHolder
		return itemHolder
	}

	override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
		val item = mAboutItems[position]
		if (item != null) {
			val aboutItemViewHolder = holder as AboutItemViewHolder
			aboutItemViewHolder.itemImage.setImageResource(item.icon)
			aboutItemViewHolder.itemName.text = item.title
		}
	}

	override fun getItemCount(): Int {
		return mAboutItems.size
	}

	override fun onClick(view: View) {
		val holder = view.tag as RecyclerView.ViewHolder
		val position = holder.layoutPosition
		mItemClickListener.aboutItemClicked(position)
	}

}