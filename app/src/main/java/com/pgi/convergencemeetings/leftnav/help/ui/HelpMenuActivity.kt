package com.pgi.convergencemeetings.leftnav.help.ui

import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import butterknife.ButterKnife
import butterknife.OnClick
import com.pgi.convergencemeetings.R
import com.pgi.convergencemeetings.base.ui.BaseActivity

class HelpMenuActivity : BaseActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.help_menu)
		ButterKnife.bind(this)
		val window = this.window
		// clear FLAG_TRANSLUCENT_STATUS flag:
		window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
		// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
		window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
		// finally change the color
		window.statusBarColor = ContextCompat.getColor(this, R.color.help_menu_line_seperator)
	}

	@OnClick(R.id.iv_help_close)
	fun onHelpClose() {
		finish()
	}

	@OnClick(R.id.tv_call)
	fun onCall() {
		Toast.makeText(this, "Call support not implemented yet", Toast.LENGTH_LONG).show()
	}

	@OnClick(R.id.tv_email)
	fun onEmail() {
		Toast.makeText(this, "Email support not implemented yet", Toast.LENGTH_LONG).show()
	}

	@OnClick(R.id.tv_visit)
	fun onVisit() {
		Toast.makeText(this, "Visit support not implemented yet", Toast.LENGTH_LONG).show()
	}
}