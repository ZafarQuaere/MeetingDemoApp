package com.pgi.convergencemeetings.leftnav.settings.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.pgi.convergencemeetings.R
import com.pgi.convergencemeetings.models.Setting

/**
 * Adapter for settings screen. Populate the setting options available for user like setting
 * default meeting room and updating name.
 */
class SettingListAdapter(private val mSettingItems: List<Setting>,
												 private val mItemClickListener: SettingItemClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), View.OnClickListener {

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
		val mContext = parent.context
		val layout = LayoutInflater.from(mContext).inflate(R.layout.setting_list_item, parent, false)
		val itemHolder = SettingItemViewHolder(layout, this)
		layout.tag = itemHolder
		return itemHolder
	}

	override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
		if(mSettingItems.isNotEmpty()) {
			val item = mSettingItems[position]
			val settingItemViewHolder = holder as SettingItemViewHolder
			settingItemViewHolder.ivCaratIcon.setImageResource(item.icon)
			settingItemViewHolder.tvItemName.text = item.title
			if (item.isMultiLabel) {
				settingItemViewHolder.tvSubListLabel.visibility = View.VISIBLE
				settingItemViewHolder.tvSubListLabel.text = item.getmSubTitle()
			} else {
				settingItemViewHolder.tvSubListLabel.visibility = View.GONE
			}
		}
	}

	override fun getItemCount(): Int {
		return mSettingItems.size
	}

	override fun onClick(view: View) {
		val holder = view.tag as RecyclerView.ViewHolder
		val position = holder.layoutPosition
		mItemClickListener.settingItemClicked(position)
	}

	/**
	 * The type Setting item view holder.
	 */
	internal inner class SettingItemViewHolder(itemView: View, listener: View.OnClickListener?) : RecyclerView.ViewHolder(itemView) {
		@BindView(R.id.iv_about_carat_icon)
		lateinit var ivCaratIcon: ImageView

		@BindView(R.id.tv_list_label)
		lateinit var tvItemName: TextView

		@BindView(R.id.tv_list_sublabel)
		lateinit var tvSubListLabel: TextView

		/**
		 * Instantiates a new Setting item view holder.
		 *
		 * @param itemView the item view
		 * @param listener the listener
		 */
		init {
			ButterKnife.bind(this, itemView)
			itemView.setOnClickListener(listener)
		}
	}

}