package com.pgi.convergence.home.ui

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import com.pgi.convergence.constants.AppConstants
import com.pgi.convergence.data.model.home.HomeCardData
import com.pgi.convergence.home.databinding.UcchomecardBinding
import com.pgi.convergence.utils.CommonUtils
import com.pgi.convergence.utils.DiffUtilsDiffCallback
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.serialization.UnstableDefault
import java.util.Collections.emptyList

/**
 * Created by Sudheer Chilumula on 2018-11-13.
 * PGi
 * sudheer.chilumula@pgi.com
 */
@UnstableDefault
@ExperimentalCoroutinesApi
class HomeCardAdapter(@LayoutRes private var layoutId: Int, private var viewModel: HomeCardsViewModel) :
  androidx.recyclerview.widget.RecyclerView.Adapter<HomeCardAdapter.CardHolder>() {

  var cardsData: List<HomeCardData> = emptyList()

  override fun getItemCount(): Int {
    return cardsData.size
  }

  override fun onBindViewHolder(holder: CardHolder, position: Int) {
    holder.bind(viewModel, holder.adapterPosition)
    holder.mItem = cardsData[holder.adapterPosition]
    holder.binding.cardContent.setOnLongClickListener {
      CommonUtils.copyRoomURL(holder.binding.cardsViewModel?.appComponent, this.cardsData[holder.adapterPosition].profile?.subhead?.text, AppConstants.COPY_URL_UNIVERSAL)
      true
    }
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardHolder {
    val layoutInflater = LayoutInflater.from(parent.context)
    val binding = DataBindingUtil.inflate<UcchomecardBinding>(
      layoutInflater, viewType, parent,
      false
    )
    return CardHolder(binding)
  }

  override fun getItemViewType(position: Int): Int {
    return layoutId
  }

  @ExperimentalCoroutinesApi
   class CardHolder(val binding: UcchomecardBinding) :
    androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root) {
    fun bind(viewModel: HomeCardsViewModel, position: Int) {
      binding.cardsViewModel = viewModel
      binding.cardData = viewModel.getCardAtIndex(position)
      binding.executePendingBindings()
      val mProfileImage: CircleImageView? = binding.cardEventProfile.profileThumbnail
      val mInitials: TextView? = binding.cardEventProfile.profileInitial
      val profileImage = mProfileImage
      val initials: TextView? = mInitials
      val profile = viewModel.getCardAtIndex(position).profile
      profile?.id?.let { viewModel.getSuggestResults(it) }
       profilePic = viewModel.map.get(profile?.id)
      if(profilePic != null) {
        initials?.visibility = View.GONE
        profileImage?.visibility = View.VISIBLE
        profile?.thumbnail = profilePic
        viewModel.loadProfileImageHome(profileImage, profilePic)
      }
      else {
        profileImage?.visibility = View.GONE
        initials?.visibility = View.VISIBLE
      }
    }
    var mItem: HomeCardData? = null
    internal var profilePic: String? = null
  }

  /*Calculates the difference between the new and previous list
    then dispatches the results to the adapter */
  
  fun setData(newCardData: List<HomeCardData>) {
    val diffCallback = DiffUtilsDiffCallback(cardsData, newCardData)
    val diffResult = DiffUtil.calculateDiff(diffCallback)
    diffResult.dispatchUpdatesTo(this)
  }

}