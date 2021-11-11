package com.pgi.convergence.utils

import androidx.annotation.Nullable
import androidx.recyclerview.widget.DiffUtil
import com.pgi.convergence.data.model.home.HomeCardData

class DiffUtilsDiffCallback(private val oldList: List<HomeCardData>, private val newList: List<HomeCardData>) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]

    }
    override fun areContentsTheSame(oldPosition: Int, newPosition: Int): Boolean {
        return oldList[oldPosition].cardHeader?.icon == newList[newPosition].cardHeader?.icon
                && oldList[oldPosition].cardHeader?.header == newList[newPosition].cardHeader?.header
                && oldList[oldPosition].profile?.subhead?.text == newList[newPosition].profile?.subhead?.text
                && oldList[oldPosition].profile?.supportText == newList[newPosition].profile?.supportText
                && oldList[oldPosition].profile?.header == newList[newPosition].profile?.header
                && oldList[oldPosition].profile?.thumbnail == newList[newPosition].profile?.thumbnail
                && oldList[oldPosition].timeStatus == newList[newPosition].timeStatus
    }

    @Nullable
    override fun getChangePayload(oldPosition: Int, newPosition: Int): Any? {
        return super.getChangePayload(oldPosition, newPosition)
    }
}