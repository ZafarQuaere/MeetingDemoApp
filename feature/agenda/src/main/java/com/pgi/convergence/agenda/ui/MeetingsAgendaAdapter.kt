package com.pgi.convergence.agenda.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.pgi.convergence.agenda.R
import com.pgi.convergence.agenda.databinding.AgendaRecycleviewCellsBinding
import com.pgi.convergence.application.CoreApplication
import com.pgi.convergence.constants.AppConstants
import com.pgi.convergence.data.model.home.HomeCardData
import com.pgi.convergence.utils.CommonUtils
import com.pgi.convergence.utils.DiffUtilsDiffCallback
import com.pgi.logging.enums.LogEvent
import com.pgi.logging.enums.LogEventValue
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.serialization.UnstableDefault
import java.util.*
import kotlin.collections.ArrayList


@UnstableDefault
@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi

class MeetingsAgendaAdapter(@LayoutRes private var layoutId: Int, private var viewModel: MeetingsAgendaViewModel) : RecyclerView.Adapter<MeetingsAgendaAdapter.ViewHolder>() {
    private val TAG = MeetingsAgendaAdapter::class.java.name
    var agenda: List<HomeCardData> = ArrayList<HomeCardData>(Arrays.asList())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflatedView = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<AgendaRecycleviewCellsBinding>(inflatedView, viewType, parent, false)
        return ViewHolder(binding)

    }

    override fun getItemViewType(position: Int): Int {
        return layoutId
    }


    override fun getItemCount() = agenda.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        try {
            holder.bind(viewModel, holder.adapterPosition)
            if (agenda.isNotEmpty() && agenda.size > holder.adapterPosition) {
                holder.mItem = agenda[holder.adapterPosition]
            }
            holder.binding.tvMeetingUrl.setOnLongClickListener {
                CommonUtils.copyRoomURL(holder.binding.agendaViewModel?.appComponent, this.agenda[holder.adapterPosition].profile?.subhead?.text, AppConstants.COPY_URL_UNIVERSAL)
                true
            }
        } catch (e:Exception) {
            CoreApplication.mLogger.error(TAG, LogEvent.EXCEPTION, LogEventValue.HOME, e.message.toString())
        }
    }

    class ViewHolder(val binding: AgendaRecycleviewCellsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(viewModel: MeetingsAgendaViewModel, position: Int) {
            if (!viewModel.isAgendaEmpty()) {
                binding.agendaViewModel = viewModel
                binding.agenda = viewModel.getAgendaCellAtIndex(position)
                binding.executePendingBindings()
                val height = viewModel.appComponent?.resources?.getDimension(R.dimen.dp_88)?.toInt()
                val mProfileImage: CircleImageView? = binding.ivAvatarAgenda
                val mInitials: AppCompatTextView? = binding.tvNameInitials
                val profileImage = mProfileImage
                val initials: AppCompatTextView? = mInitials
                val profile = viewModel.getAgendaCellAtIndex(position).profile
                itemView.minimumHeight = height!!
                profile?.id?.let { viewModel.getSuggestResults(it) }
                profilePic = viewModel.map.get(profile?.id)
                if (profilePic != null) {
                    initials?.visibility = View.INVISIBLE
                    profileImage?.visibility = View.VISIBLE
                    profile?.thumbnail = profilePic
                    viewModel.loadProfileImageHome(profileImage, profilePic)
                } else {
                    profileImage?.visibility = View.INVISIBLE
                    initials?.visibility = View.VISIBLE
                }
            }
        }

        var mItem: HomeCardData? = null
        internal var profilePic: String? = null

    }

    /*Calculates the difference between the new and previous list
    then dispatches the results to the adapter */
    fun setData(newCardData: List<HomeCardData>) {
        val diffCallback = DiffUtilsDiffCallback( agenda, newCardData)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        diffResult.dispatchUpdatesTo(this)
    }
}