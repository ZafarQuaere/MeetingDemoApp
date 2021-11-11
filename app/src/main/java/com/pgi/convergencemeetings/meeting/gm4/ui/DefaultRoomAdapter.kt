package com.pgi.convergencemeetings.meeting.gm4.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.pgi.convergencemeetings.R
import com.pgi.convergencemeetings.models.DefaultRoom

/**
 * Created by ashwanikumar on 12/12/2017.
 */
class DefaultRoomAdapter(private val mDefaultItems: List<DefaultRoom>,
												 private val mItemClickListener: DefaultRoomItemClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
	private var mContext: Context? = null
	private var mLastSelectedRoom: DefaultRoom? = null
	private var mCurrentSelectedPosition = 0

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
		mContext = parent.context
		val layout = LayoutInflater.from(mContext).inflate(R.layout.default_room_item, parent, false)
		val itemHolder = SettingItemViewHolder(layout)
		layout.tag = itemHolder
		return itemHolder
	}

	override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
		if (mDefaultItems.isNotEmpty()) {
			val item = mDefaultItems[position]
			val settingItemViewHolder = holder as SettingItemViewHolder
			if (item.isDefaultRoom) {
				settingItemViewHolder.ivCaratIcon.visibility = View.VISIBLE
				mLastSelectedRoom = item
				mCurrentSelectedPosition = position
			} else {
				settingItemViewHolder.ivCaratIcon.visibility = View.GONE
			}
			settingItemViewHolder.tvItemName.text = item.title
			settingItemViewHolder.tvSubListLabel.text = item.subTitle
			settingItemViewHolder.mView.setOnClickListener {
			handleListItemClick(position)
			}
		}
	}

	private fun handleListItemClick(position: Int) {
		if (mCurrentSelectedPosition != position) {
			mLastSelectedRoom?.isDefaultRoom = false
			mDefaultItems[position].isDefaultRoom = true
			mLastSelectedRoom = mDefaultItems[position]
			notifyDataSetChanged()
			mCurrentSelectedPosition = position
			mItemClickListener.defaultRoomSelected(mLastSelectedRoom?.getmMeetingRoomId())
		}
	}

	override fun getItemCount(): Int {
		return mDefaultItems.size
	}

	internal inner class SettingItemViewHolder(var mView: View) : RecyclerView.ViewHolder(mView) {
    @BindView(R.id.iv_about_carat_icon)
		lateinit var ivCaratIcon: ImageView

    @BindView(R.id.tv_list_label)
		lateinit var tvItemName: TextView

    @BindView(R.id.tv_list_sublabel)
		lateinit var tvSubListLabel: TextView

		init {
			ButterKnife.bind(this, mView)
		}
	}

}