package com.pgi.convergence.utils

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.convergence.core.R
import com.google.gson.Gson
import com.pgi.convergence.RobolectricTest
import com.pgi.convergence.TestApplication
import com.pgi.convergence.TestLogger
import com.pgi.convergence.application.CoreApplication
import com.pgi.convergence.persistance.SharedPreferencesManager
import com.pgi.logging.Logger
import kotlinx.android.synthetic.main.custom_snack_bar.*
import okhttp3.mockwebserver.MockWebServer
import org.junit.*

import org.junit.Assert.*
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.powermock.api.mockito.PowerMockito
import org.powermock.modules.junit4.rule.PowerMockRule
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@Config(manifest = Config.NONE, application = TestApplication::class)
class SimpleCustomSnackbarTest: RobolectricTest(){

    @get:Rule
    val rule: PowerMockRule = PowerMockRule()

    @Mock
    private var mSimpleCustomSnackbar: SimpleCustomSnackbar? = null

    @Mock
    private var listener: View.OnClickListener? = null

    @Mock
    private var mCoordinatorLayout: CoordinatorLayout? = null

    @Mock
    private var mFrameLayout: FrameLayout? = null

    @Mock
    private var mLinearLayout: LinearLayout? = null

    @Mock
    private var view: View? = null



    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        val context = RuntimeEnvironment.application.applicationContext

        view = View(context)
        mCoordinatorLayout = CoordinatorLayout(context)
        mFrameLayout = FrameLayout(context)
        mLinearLayout = LinearLayout(context)
        CoreApplication.appContext = context
        view = View(context)
    }

    @Test
    fun `test guest waiting joined `() {
        SimpleCustomSnackbarView.test = true
        SimpleCustomSnackbar.make(mCoordinatorLayout!!, "WAIT", 0 , listener,"TURN OFF WAIT", 0)
        mCoordinatorLayout?.rootView?.findViewById<TextView>(R.id.tv_message)?.text?.equals("WAIT")?.let { assertTrue(it) }
        mCoordinatorLayout?.rootView?.findViewById<TextView>(R.id.tv_action)?.text?.equals("TURN OFF WAIT")?.let { assertTrue(it) }

    }

    @Test
    fun `test guest waiting joined frame layout `() {
        SimpleCustomSnackbarView.test = true
        SimpleCustomSnackbar.make(mFrameLayout!!, "WAIT", 0 , listener,"TURN OFF WAIT", 0)
        mFrameLayout?.rootView?.findViewById<TextView>(R.id.tv_message)?.text?.equals("WAIT")?.let { assertTrue(it) }
        mFrameLayout?.rootView?.findViewById<TextView>(R.id.tv_action)?.text?.equals("TURN OFF WAIT")?.let { assertTrue(it) }
    }

    @Test
    fun `test guest waiting joined no test  `() {
        SimpleCustomSnackbarView.test = false
        SimpleCustomSnackbar.make(mCoordinatorLayout!!, "WAIT", 0 , listener,"TURN OFF WAIT", 0)
        mCoordinatorLayout?.rootView?.findViewById<TextView>(R.id.tv_message)?.text?.equals("WAIT")?.let { assertTrue(it) }
        mCoordinatorLayout?.rootView?.findViewById<TextView>(R.id.tv_action)?.text?.equals("TURN OFF WAIT")?.let { assertTrue(it) }

    }
}