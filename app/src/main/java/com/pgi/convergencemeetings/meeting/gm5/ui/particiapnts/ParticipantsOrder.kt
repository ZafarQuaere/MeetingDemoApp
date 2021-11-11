package com.pgi.convergencemeetings.meeting.gm5.ui.particiapnts

import androidx.core.text.isDigitsOnly
import com.pgi.convergence.constants.AppConstants
import com.pgi.convergencemeetings.meeting.gm5.data.model.User

object ParticipantsOrder {

    val guestSortingOrderList = listOf<String>(AppConstants.IS_SELF_USER, AppConstants.IS_CONNECTED,AppConstants.OTHER_USER,AppConstants.NOT_CONNECTED)

    fun getSortedList(mUserList: MutableList<User>): MutableList<User> {
        val guestList = mutableListOf<User>()
        val hostList = mutableListOf<User>()
        val cohostList = mutableListOf<User>()
        val presenterList = mutableListOf<User>()
        val guestOrderedList = mutableListOf<User>()
        val remainingGuestList = mutableListOf<User>()
        if (mUserList.isNotEmpty() && mUserList.size > 0){
            mUserList.forEachIndexed { index, user ->
                when (user.roomRole) {
                    AppConstants.HOST -> if (user.delegateRole) {
                        cohostList.add(user)
                    } else {
                        hostList.add(user)
                    }

                    AppConstants.GUEST -> if (user.promoted) {
                        presenterList.add(user)
                    } else if (!checkUserforWebpresence(user)) {
                        user.firstName?.let {
                            if (it.isDigitsOnly()) {
                                //all dial-in user join from number
                                remainingGuestList.add(user)
                            } else {
                                guestOrderedList.add(user)
                            }
                        }
                    } else {
                        // all user who have webpresence as left
                        remainingGuestList.add(user)
                    }
                    else -> guestList.add(user)
                }
            }
        }

        // filter all list based on name and then self and then audio connection
        val orderById = guestSortingOrderList.withIndex().associate { it.value to it.index }
        val compareListByRoomRole = compareBy<User> { orderById[getSortingOrder(it)] }
        cohostList.sortBy { it.name?.toLowerCase() }
        cohostList.sortWith(compareListByRoomRole)
        hostList.sortBy { it.name?.toLowerCase() }
        hostList.sortWith(compareListByRoomRole)
        presenterList.sortBy { it.name?.toLowerCase() }
        presenterList.sortWith(compareListByRoomRole)
        guestOrderedList.sortBy { it.name?.toLowerCase() }
        guestOrderedList.sortWith(compareListByRoomRole)

        guestList.addAll(hostList)
        guestList.addAll(cohostList)
        guestList.addAll(presenterList)
        guestList.addAll(guestOrderedList)
        remainingGuestList.sortBy { it.name?.toLowerCase() }
        guestList.addAll(remainingGuestList)

        return guestList
    }

    public fun checkUserforWebpresence(element: User): Boolean {
        return (element.id == element.audio.id) && !(element.firstName?.isDigitsOnly() ?: false)
    }

    //	get sorting order based on audio connection and self user
    public fun getSortingOrder(user: User): String {
        user.let {
            if (it.isSelf) {
                return AppConstants.IS_SELF_USER
            } else if (it.audio.isConnected) {
                return AppConstants.IS_CONNECTED
            } else if (!it.audio.isConnected) {
                return AppConstants.NOT_CONNECTED
            }
        }
        return AppConstants.OTHER_USER
    }
}