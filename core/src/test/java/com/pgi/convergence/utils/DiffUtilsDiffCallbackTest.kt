package com.pgi.convergence.utils

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pgi.convergence.data.enums.home.HomeCardType
import com.pgi.convergence.data.enums.msal.MsalMeetingStatus
import com.pgi.convergence.data.model.home.HomeCardData
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import java.util.*


@RunWith(AndroidJUnit4::class)
@Config(manifest = Config.NONE)
class DiffUtilsDiffCallbackTest {
    val cardata = HomeCardData(
            "AAMkAGVmMDEzMTM4LTZmYWUtNDdkNC1hMDZiLTU1OGY5OTZhYmY4OAFRAAgI1snaIDaAAEYAAAAAIkPFveuwe0ygY4Mfa1RFEQcAIiLKjG2I7E_Xv0_ys6MD0wAAAAABDQAAIiLKjG2I7E_Xv0_ys6MD0wAAGG7PGQAAEA",
            HomeCardType.UPCOMING_MEETINGS,
            MsalMeetingStatus.NONE, null, null, null, null, null, null,
            null, null, null)
    val newcardata = HomeCardData(
            "AAMkAGVmMDEzMTM4LTZmYWUtNDdkNC1hMDZiLTU1OGY5OTZhYmY4OAFRAAgI1snaIDaAAEYAAAAAIkPFveuwe0ygY4Mfa1RFEQcAIiLKjG2I7E_Xv0_ys6MD0wAAAAABDQAAIiLKjG2I7E_Xv0_ys6MD0wAAGG7PGQAAEA",
            HomeCardType.UPCOMING_MEETINGS,
            MsalMeetingStatus.NONE, null, null, null, null, null, null,
            null, null, null)
    val secondcardata = HomeCardData(
            "AAMkAGVmMDEzMTM4LTZmYWUtNDdkNC1hMDZiLTU1OGY5OTZhYmY4OAFRAAgI1snaIDaAAEYAAAAAIkPFveuwe0ygY4Mfa1RFEQcAIiLKjG2I7E_Xv0_ys6MD0wAAAAABDQAAIiLKjG2I7E_Xv0_ys6MD0wAAGG7PGQAAEA",
            HomeCardType.UPCOMING_MEETINGS,
            MsalMeetingStatus.NONE, null, null, null, null, null, null,
            null, null, null)
    val secondnewcardata = HomeCardData(
            "AAMkAGVmMDEzMTM4LTZmYWUtNDdkNC1hMDZiLTU1OGY5OTZhYmY4OAFRAAgI1snaIDaAAEYAAAAAIkPFveuwe0ygY4Mfa1RFEQcAIiLKjG2I7E_Xv0_ys6MD0wAAAAABDQAAIiLKjG2I7E_Xv0_ys6MD0wAAGG7PGQAAEA",
            HomeCardType.UPCOMING_MEETINGS,
            MsalMeetingStatus.NONE, null, null, null, null, null, null,
            null, null, null)
    var aList = Arrays.asList(secondcardata, secondnewcardata)
    var anotherList = Arrays.asList(cardata, newcardata)
    var homeDataList= aList
    var newHomeDataList= anotherList
    var diffCallback: DiffUtilsDiffCallback? = null


    @Before
    fun setUp() {
        diffCallback=  DiffUtilsDiffCallback(homeDataList, newHomeDataList )

    }

    @Test
    fun getOldListSize() {
        diffCallback?.oldListSize
        Assert.assertEquals(homeDataList.size , 2)
    }

    @Test
    fun getNewListSize() {
        diffCallback?.newListSize
        newHomeDataList.size
        Assert.assertEquals(newHomeDataList.size , 2)
    }

    @Test
    fun areItemsTheSame() {
        diffCallback?.areItemsTheSame(0 , 1 )
        diffCallback?.areItemsTheSame(0 , 1 )?.let { Assert.assertTrue(it) }
    }

    @Test
    fun areContentsTheSame() {
        diffCallback?.areContentsTheSame(0 , 1 )
        diffCallback?.areContentsTheSame(0 , 1 )?.let { Assert.assertTrue(it) }


    }

    @Test
    fun getChangePayload() {
        diffCallback?.getChangePayload(0, 1)
    }
}