package com.pgi.convergencemeetings.meeting.gm5.ui.audio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.newrelic.agent.android.NewRelic
import com.pgi.convergencemeetings.R
import com.pgi.logging.enums.Interactions

/**
 * Created by ashwanikumar on 9/27/2017.
 */
class AudioSelectionProgressFragment : Fragment() {
  @BindView(R.id.shimmer_view_group)
	lateinit var mShimmerViewGroup: ViewGroup

	var mShimmerLayout: ShimmerLayout? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		NewRelic.setInteractionName(Interactions.AUDIO_SELECTION_PROGRESS.interaction)
		super.onCreate(savedInstanceState)
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		val view = inflater.inflate(R.layout.fragment_audio_selection_progress_bar, container, false)
		ButterKnife.bind(this, view)
		mShimmerLayout = context?.let { ShimmerLayout(it) }
		mShimmerLayout?.layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
		mShimmerViewGroup.addView(mShimmerLayout)
		return view
	}

	override fun onResume() {
		super.onResume()
		mShimmerViewGroup.visibility = View.VISIBLE
		mShimmerLayout?.startShimmerAnimation()
	}

	override fun onStop() {
		super.onStop()
		mShimmerViewGroup.visibility = View.GONE
		mShimmerLayout?.stopShimmerAnimation()
	}

	@OnClick(R.id.btn_close_shimmer)
	fun handleShimmerCloseClick() {
		onStop()
		(activity as AudioSelectionShimmerContractor.activity).handleCloseShimmerClick()
	}
}