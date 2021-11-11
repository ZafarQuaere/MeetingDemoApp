package com.pgi.convergencemeetings.base.ui.retry

import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import org.greenrobot.greendao.annotation.NotNull

/**
 * Created by nnennaiheke on 3/14/18.
 */
object Fragments {
	fun showDialogAllowingStateLoss(@NotNull fragmentManager: FragmentManager,
																	@NotNull dialogFragment: DialogFragment, @NotNull tag: String?) {
		fragmentManager.beginTransaction()
				.add(dialogFragment, tag)
				.commitAllowingStateLoss()
	}
}