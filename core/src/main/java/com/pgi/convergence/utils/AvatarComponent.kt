package com.pgi.convergence.utils

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.annotation.VisibleForTesting
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.constraintlayout.widget.Guideline
import com.convergence.core.R
import com.pgi.convergence.constants.AppConstants
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class AvatarComponent @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val TAG = AvatarComponent::class.java.simpleName
    private var userCount: Int = 0
    private var viewHeight = 0
    private var viewWidth = 0
    private var parentLayout : ConstraintLayout

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    var avatarIds = mutableListOf<CircleImageView>()

    var avatarDrawables = mutableListOf<Int>()

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    var textUserCount: TextView? = null

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    var textNameInitial: TextView? = null

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.avatar_view, this, false)
        val set = ConstraintSet()
        addView(view)
        set.clone(this)
        set.match(view, this)
         parentLayout = findViewById<View>(R.id.rootView) as ConstraintLayout
        textUserCount = findViewById<TextView>(R.id.textUserCount)
        textNameInitial = findViewById<TextView>(R.id.textNameInitial)
        addDefaultDrawables()
        setAvatar()
    }

    private fun setAvatar() {
         if (userCount == 1) {
             viewWidth = resources.getDimension(R.dimen.av_default_width).toInt()
             viewHeight= resources.getDimension(R.dimen.av_default_width).toInt()
             createAvatarIcon(1, viewHeight, viewWidth)
             val constraintSet = createConstraintSet()
             constraintSet.setTopTopToStartStart(avatarIds[0], parentLayout, parentLayout)
             constraintSet.setEndToEnd(avatarIds[0], parentLayout)
             constraintSet.setBottomToBottom(avatarIds[0], parentLayout)
             constraintSet.applyTo(parentLayout)
             textNameInitial?.visibility = View.VISIBLE
         }

        if (userCount == 2) {
            viewWidth = resources.getDimension(R.dimen.av_width_2).toInt()
            viewHeight= resources.getDimension(R.dimen.av_height_2).toInt()
            val guidelineHorizontal = findViewById<Guideline>(R.id.guideline2)
            val guidelineVertical = findViewById<Guideline>(R.id.guideline1)
            createAvatarIcon(2, viewHeight, viewWidth)
            val constraintSet = createConstraintSet()
            constraintSet.setTopTopToStartStart(avatarIds[0], parentLayout, parentLayout)
            constraintSet.setTopBottomToStartStart(avatarIds[1], guidelineHorizontal, guidelineVertical)
            constraintSet.applyTo(parentLayout)
        }

        if (userCount == 3) {
            viewWidth = resources.getDimension(R.dimen.av_user_width).toInt()
            viewHeight= resources.getDimension(R.dimen.av_user_height).toInt()
            val guidelineHorizontal = findViewById<Guideline>(R.id.guideline3)
            createAvatarIcon(3, viewHeight, viewWidth)
            val constraintSet = createConstraintSet()
            constraintSet.setTopTopToStartStart(avatarIds[0], parentLayout, parentLayout)
            constraintSet.setEndToEnd(avatarIds[0], parentLayout)
            constraintSet.setTopBottomToStartStart(avatarIds[1], guidelineHorizontal, parentLayout)
            constraintSet.setTopBottomToStartEnd(avatarIds[2], guidelineHorizontal, avatarIds[1])
            constraintSet.applyTo(parentLayout)
        }

        if (userCount >= 4) {
            viewWidth = resources.getDimension(R.dimen.av_user_width).toInt()
            viewHeight= resources.getDimension(R.dimen.av_user_height).toInt()
            createAvatarIcon(4, viewHeight, viewWidth)
            val constraintSet = createConstraintSet()
            constraintSet.setTopTopToStartStart(avatarIds[0], parentLayout, parentLayout)
            constraintSet.setTopTopToStartEnd(avatarIds[1], parentLayout, avatarIds[0])
            constraintSet.setTopBottomToStartStart(avatarIds[2], avatarIds[0], parentLayout)
            constraintSet.setTopBottomToStartEnd(avatarIds[3], avatarIds[0], avatarIds[2])
            constraintSet.match(textUserCount as View, avatarIds[3])
            constraintSet.applyTo(parentLayout)
            if (userCount > 4) {
                textUserCount?.visibility = View.VISIBLE
                textUserCount?.text = (userCount - 3).toString()
                avatarIds[3].visibility = View.INVISIBLE
            } else {
                textUserCount?.visibility = View.GONE
                avatarIds[3].visibility = View.VISIBLE
            }
        }
    }

    fun setAvatarIcon(count: Int , profileList: MutableList<String?>, profileInitials : MutableList<String?>? = null) {
        refreshView()
        userCount = count
        setAvatar()
        setAvatarValues(profileList, profileInitials)
    }

    private fun refreshView() {
        for (avatar in avatarIds) {
            parentLayout.removeView(avatar)
        }
        avatarIds.clear()
        textUserCount?.visibility = View.GONE
        textNameInitial?.visibility = View.GONE
        userCount = 0
    }

    private fun ConstraintSet.match(view: View, parentView: View) {
        this.connect(view.id, ConstraintSet.TOP, parentView.id, ConstraintSet.TOP)
        this.connect(view.id, ConstraintSet.START, parentView.id, ConstraintSet.START)
        this.connect(view.id, ConstraintSet.END, parentView.id, ConstraintSet.END)
        this.connect(view.id, ConstraintSet.BOTTOM, parentView.id, ConstraintSet.BOTTOM)
    }

    private fun setAvatarValues(avatarPic: MutableList<String?>, avatarInitials: MutableList<String?>? = null) {
        val sortedProfilePics: MutableList<String?> = mutableListOf()
        if (userCount > 1) {
            val filteredList = avatarPic.filterNotNull()
            val filteredNullList: List<String?> = avatarPic.filter {
                it == null
            }
            sortedProfilePics.addAll(filteredList)
            sortedProfilePics.addAll(filteredNullList)
        }
        for (pos in 0 until avatarIds.size) {
            if (userCount == 1) {
                setUserInitialsOrPic(avatarIds[pos], textNameInitial, avatarPic[pos], avatarInitials?.get(0))
            }
            else {
                setUserPic(avatarIds[pos], sortedProfilePics[pos])
            }
        }
    }

    private fun setUserInitialsOrPic(profileView: CircleImageView?, initialView : TextView?, profilePic : String?, profileInitial : String?) {
        initialView?.text = profileInitial
        initialView?.visibility = View.VISIBLE
        profileView?.visibility = View.INVISIBLE
        initialView?.setBackgroundResource(R.drawable.recent_meetings_circle_drawable)
        if (!profilePic.isNullOrEmpty()) {
            Picasso.get().load(profilePic).fit().into(profileView, object : Callback {
                override fun onSuccess() {
                    profileView?.visibility = View.VISIBLE
                    initialView?.visibility = View.INVISIBLE
                }
                override fun onError(e: Exception) {
                    Log.e(TAG, "ChatListAdapter: error getting profile picture for participant")
                }
            })
        } else if (profileInitial.equals(AppConstants.POUND_SYMBOL)) {
            initialView?.setBackgroundResource(R.drawable.avatar59)
            initialView?.text = ""
        } else {
            initialView?.setBackgroundResource(R.drawable.recent_meetings_circle_drawable)
            initialView?.text = profileInitial
        }
    }

    private fun setUserPic(profileView: CircleImageView?, profilePic : String?) {
        if (!profilePic.isNullOrEmpty()) {
            Picasso.get().load(profilePic).fit().into(profileView)
        }
    }

    private fun createAvatarIcon(noOfAvatar: Int, height: Int, width: Int) {
        for (i in 0 until noOfAvatar) {
            val avatar = CircleImageView(context)
            val avatarParams = LayoutParams(height, width)
            avatar.apply {
                this.id = View.generateViewId()
                this.layoutParams = avatarParams
                this.setImageDrawable(context.getDrawable(avatarDrawables[i]))
                this.visibility = View.VISIBLE
            }
            parentLayout.addView(avatar)
            avatarIds.add(avatar)
        }
    }

    private fun createConstraintSet(): ConstraintSet {
        val constraintSet = ConstraintSet()
        constraintSet.clone(parentLayout)
        return constraintSet
    }

    private fun ConstraintSet.setEndToEnd(view: View, parentView: View) {
        this.connect(view.id, ConstraintSet.END, parentView.id, ConstraintSet.END)
    }

    private fun ConstraintSet.setBottomToBottom(view: View, parentView: View) {
        this.connect(view.id, ConstraintSet.BOTTOM, parentView.id, ConstraintSet.BOTTOM)
    }

    private fun ConstraintSet.setTopBottomToStartStart(view: View, topParentView: View, startParentView: View) {
        this.connect(view.id, ConstraintSet.TOP, topParentView.id, ConstraintSet.BOTTOM)
        this.connect(view.id, ConstraintSet.START, startParentView.id, ConstraintSet.START)
    }

    private fun ConstraintSet.setTopTopToStartStart(view: View, topParentView: View, startParentView: View) {
        this.connect(view.id, ConstraintSet.TOP, topParentView.id, ConstraintSet.TOP)
        this.connect(view.id, ConstraintSet.START, startParentView.id, ConstraintSet.START)
    }

    private fun ConstraintSet.setTopTopToStartEnd(view: View, topParentView: View, startParentView: View) {
        this.connect(view.id, ConstraintSet.TOP, topParentView.id, ConstraintSet.TOP)
        this.connect(view.id, ConstraintSet.START, startParentView.id, ConstraintSet.END)
    }

    private fun ConstraintSet.setTopBottomToStartEnd(view: View, topParentView: View, startParentView: View) {
        this.connect(view.id, ConstraintSet.TOP, topParentView.id, ConstraintSet.BOTTOM)
        this.connect(view.id, ConstraintSet.START, startParentView.id, ConstraintSet.END)
    }

    private fun addDefaultDrawables() {
        avatarDrawables.add(R.drawable.avatar59)
        avatarDrawables.add(R.drawable.ic_avatar_orange)
        avatarDrawables.add(R.drawable.ic_avatar_green)
        avatarDrawables.add(R.drawable.ic_avatar_red)
    }
}