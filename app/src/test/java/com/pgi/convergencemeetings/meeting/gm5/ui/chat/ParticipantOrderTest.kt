package com.pgi.convergencemeetings.meeting.gm5.ui.chat

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pgi.convergence.application.CoreApplication
import com.pgi.convergence.constants.AppConstants
import com.pgi.convergencemeetings.application.TestApplication
import com.pgi.convergencemeetings.meeting.gm5.data.model.Audio
import com.pgi.convergencemeetings.meeting.gm5.data.model.User
import com.pgi.convergencemeetings.meeting.gm5.ui.particiapnts.ParticipantsOrder
import io.mockk.MockKAnnotations
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(manifest = Config.NONE, application = TestApplication::class)
class ParticipantOrderTest {

    private var context: Context = ApplicationProvider.getApplicationContext()

    @Before
    fun before() {
        MockKAnnotations.init(this, relaxed = true)
        MockitoAnnotations.initMocks(this)
        CoreApplication.appContext = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun `Test getSortedList when user host `() {
        val emptyList = mutableListOf<User>()
        val response = ParticipantsOrder.getSortedList(emptyList)
        Assert.assertEquals(response.size.toString(),"0")
        val users = mutableListOf<User>(User(id = "123",firstName = "Jack",delegateRole =  false),
            User(id = "123",firstName = "Jack", roomRole = AppConstants.HOST,delegateRole =  false),
            User(id = "1234",firstName = "Dorsey", roomRole = AppConstants.HOST,delegateRole = true))
        val sortedList = ParticipantsOrder.getSortedList(users)
        Assert.assertNotNull(sortedList)
        Assert.assertEquals(sortedList.size,3)
    }

    @Test
    fun `Test getSortedList when user guest `() {
        val users = mutableListOf<User>(User(id = "123",firstName = "Jack", roomRole = AppConstants.GUEST,promoted =  true), User(id = "1234",firstName = "7834908329", roomRole = AppConstants.GUEST,promoted = false, audio = Audio(id = "1234")),
             User(id = "123", firstName = "Zack", roomRole = AppConstants.GUEST, active = true,promoted = false, audio = Audio(id = "123")),
             User(id = "123", firstName = "7834908329", roomRole = AppConstants.GUEST, active = true,promoted = false, audio = Audio(id = "12345",mute = false, isConnecting = true, isConnected = false, isVoip = true)))
        val sortedList = ParticipantsOrder.getSortedList(users)
        Assert.assertNotNull(sortedList)
        Assert.assertEquals(sortedList.size,4)
    }

    @Test
    fun `Test getSortingOrder when user is self `() {
        val user = User(id = "123", roomRole = AppConstants.HOST,profileImage = null,isSelf = true,hasControls = true, promoted = true,audio = Audio(mute = false,isConnecting = false,isConnected = true,isVoip = true))
        val userType = ParticipantsOrder.getSortingOrder(user)
        assert(userType == AppConstants.IS_SELF_USER)
    }

    @Test
    fun `Test getSortingOrder when user is connected via audio `() {
        val user = User(id = "123", roomRole = AppConstants.HOST,profileImage = null,isSelf = false,hasControls = true, promoted = true,audio = Audio(mute = false,isConnecting = false,isConnected = true,isVoip = true))
        val userType = ParticipantsOrder.getSortingOrder(user)
        assert(userType == AppConstants.IS_CONNECTED)
    }

    @Test
    fun `Test getSortingOrder when user is not connected via audio `() {
        val user = User(id = "123",roomRole = AppConstants.HOST,profileImage = null,isSelf = false,hasControls = true, promoted = true,audio = Audio(mute = false,isConnecting = true,isConnected = false,isVoip = true))
        val userType = ParticipantsOrder.getSortingOrder(user)
        assert(userType == AppConstants.NOT_CONNECTED)
    }

    @Test
    fun `Test checkUserforWebpresence for audio only`(){
        val user = User(id = "123", firstName = "9845672723", roomRole = AppConstants.GUEST, profileImage = null, active = true, delegateRole = false, isSelf = false, hasControls = true, promoted = false, audio = Audio(id = "123",mute = false, isConnecting = true, isConnected = false, isVoip = true))
        val isUserWebPresent = ParticipantsOrder.checkUserforWebpresence(user)?:false
        assert(!isUserWebPresent)
    }

    @Test
    fun `Test checkUserforWebpresence for not webpresent`(){
        val user = User(id = "123", firstName = "Neha", roomRole = AppConstants.GUEST, profileImage = null, active = true, delegateRole = false, isSelf = false, hasControls = true, promoted = false, audio = Audio(id = "123",mute = false, isConnecting = false, isConnected = true, isVoip = true))
        val isUserWebPresent = ParticipantsOrder.checkUserforWebpresence(user)?:false
        assert(isUserWebPresent)
    }

    @Test
    fun `Test checkUserforWebpresence for voip user`(){
        val user = User(id = "123", firstName = "Neha", roomRole = AppConstants.GUEST, profileImage = null, active = true, delegateRole = false, isSelf = false, hasControls = true, promoted = false, audio = Audio(id = "PART1234",mute = false, isConnecting = true, isConnected = false, isVoip = true))
        val isUserWebPresent = ParticipantsOrder.checkUserforWebpresence(user)?:false
        assert(!isUserWebPresent)
    }

    @Test
    fun `Test checkUserforWebpresence for User not connected with audio`(){
        val user = User(id = "123", firstName = "Neha", roomRole = AppConstants.GUEST, profileImage = null, active = true, delegateRole = false, isSelf = false, hasControls = true, promoted = false, audio = Audio(id = null,mute = false, isConnecting = true, isConnected = false, isVoip = true))
        val isUserWebPresent = ParticipantsOrder.checkUserforWebpresence(user)?:false
        assert(!isUserWebPresent)
    }
}