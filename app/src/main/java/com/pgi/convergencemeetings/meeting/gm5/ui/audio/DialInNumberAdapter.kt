package com.pgi.convergencemeetings.meeting.gm5.ui.audio

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.pgi.convergence.constants.AppConstants
import com.pgi.convergence.utils.DialInNumbers
import com.pgi.convergencemeetings.R
import java.util.*

/**
 * Created by ashwanikumar on 11/15/2017.
 */
class DialInNumberAdapter( var context: Context,private var mDialInNumbers: List<DialInNumbers>,
						   private val mListener: DialInNumberListener?) : RecyclerView.Adapter<DialInNumberAdapter.ViewHolder>() {
	private var mCurrentSelectedPosition = 0
	private var mLastSelectedView: RadioButton? = null

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		val view = LayoutInflater.from(parent.context)
				.inflate(R.layout.dial_in_items, parent, false)
		return ViewHolder(view)
	}

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		if(mDialInNumbers.isNotEmpty()) {
			holder.mItem = mDialInNumbers[position]
			val location = holder.mItem.location
			val phoneNumber = holder.mItem.phoneNumber
			if (location != null) {
				holder.tvCntryPrmryNum.visibility = View.VISIBLE
				if (location.equals(AppConstants.PRIMARY_ACCESS_NUM, ignoreCase = true)) {
					mLastSelectedView = holder.rbIsNumberChecked
					holder.rbIsNumberChecked.isChecked = true
					mLastSelectedView = holder.rbIsNumberChecked
					holder.tvCntryPrmryNum.text = context.resources.getString(R.string.primary_access_number)
				} else {
					holder.rbIsNumberChecked.isChecked = false
					holder.tvCntryPrmryNum.text = location
				}
			}
			if (phoneNumber != null) {
				holder.tvPhoneNumber.text = phoneNumber.toUpperCase(Locale.getDefault())
			}
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
		mListener?.onPhoneNumberSelected(holder.mItem.phoneNumber)
	}

	override fun getItemCount(): Int {
		return mDialInNumbers.size
	}

	override fun getItemViewType(position: Int): Int {
		return position
	}

	inner class ViewHolder(var mView: View) : RecyclerView.ViewHolder(mView) {
		@BindView(R.id.tv_cntry_prmry_num)
		lateinit var tvCntryPrmryNum: TextView

		@BindView(R.id.tv_phone_number)
		lateinit var tvPhoneNumber: TextView

		@BindView(R.id.rb_num_checked)
		lateinit var rbIsNumberChecked: RadioButton

		lateinit var mItem: DialInNumbers

		init {
			ButterKnife.bind(this, mView)
		}
	}

	fun addSerachList(mDialInNumbersFilter: List<DialInNumbers>) {
		mDialInNumbers = mDialInNumbersFilter
		notifyDataSetChanged()
	}

}