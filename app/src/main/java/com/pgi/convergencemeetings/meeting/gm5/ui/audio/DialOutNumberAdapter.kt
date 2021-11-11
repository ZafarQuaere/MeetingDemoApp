package com.pgi.convergencemeetings.meeting.gm5.ui.audio

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.google.common.base.Strings
import com.pgi.convergence.constants.AppConstants
import com.pgi.convergencemeetings.R
import com.pgi.convergencemeetings.models.getPhoneNumberModel.Phone
import com.pgi.convergencemeetings.utils.DialOutNumberListener
import java.util.*

/**
 * Created by ashwanikumar on 11/15/2017.
 */
class DialOutNumberAdapter(private val mDialOutNumberList: List<Phone>, private val mListener: DialOutNumberListener?) : RecyclerView.Adapter<DialOutNumberAdapter.ViewHolder>() {
	private var mCurrentSelectedPosition = 0
	private var mLastSelectedView: RadioButton? = null
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		val view = LayoutInflater.from(parent.context)
				.inflate(R.layout.dial_out_items, parent, false)
		return ViewHolder(view)
	}

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		if (mDialOutNumberList.isNotEmpty()) {
			var phoneNumber = ""
			holder.mItem = mDialOutNumberList[position]
			val countryCode = holder.mItem.countryCode
			if (!Strings.isNullOrEmpty(countryCode)) {
				phoneNumber = if (!countryCode.startsWith(AppConstants.PLUS_SYMBOL)) {
					AppConstants.STRING_PLUS + countryCode
				} else {
					countryCode
				}
			}
			phoneNumber += AppConstants.BLANK_SPACE + holder.mItem.number
			if (position == 0) {
				holder.rbIsNumberChecked.isChecked = true
				mLastSelectedView = holder.rbIsNumberChecked
				mCurrentSelectedPosition = position
			} else {
				holder.rbIsNumberChecked.isChecked = false
			}
			holder.tvPhoneNumber.text = phoneNumber.toUpperCase(Locale.getDefault())
			holder.mView.setOnClickListener { handleListItemClick(position, holder) }
			holder.rbIsNumberChecked.setOnClickListener { handleListItemClick(position, holder) }
		}
	}

	private fun handleListItemClick(position: Int, holder: ViewHolder) {
		// Notify the active callbacks interface (the DialInActivityContract, if the
		// DialInFragmentContract is attached to one) that an item has been selected.
		if (mLastSelectedView != null && mCurrentSelectedPosition != position) {
			mLastSelectedView?.isChecked = false
			holder.rbIsNumberChecked.isChecked = true
			mLastSelectedView = holder.rbIsNumberChecked
		}
		mCurrentSelectedPosition = position
		mListener?.onPhoneNumberSelected(holder.mItem)
	}

	override fun getItemCount(): Int {
		return mDialOutNumberList.size
	}

	inner class ViewHolder(var mView: View) : RecyclerView.ViewHolder(mView) {
    @BindView(R.id.tv_phone_number)
		lateinit var tvPhoneNumber: TextView

    @BindView(R.id.rb_num_checked)
		lateinit var rbIsNumberChecked: RadioButton

		lateinit var mItem: Phone

		init {
			ButterKnife.bind(this, mView)
		}
	}

}