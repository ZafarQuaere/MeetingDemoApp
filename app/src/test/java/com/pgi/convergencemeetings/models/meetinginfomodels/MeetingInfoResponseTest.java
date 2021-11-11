package com.pgi.convergencemeetings.models.meetinginfomodels;

import com.pgi.convergencemeetings.RobolectricTest;
import com.pgi.convergencemeetings.models.MeetingInfoResponse;
import com.pgi.convergencemeetings.models.MeetingRoomGetResult;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

/**
 * Created by amit1829 on 8/22/2017.
 */

public class MeetingInfoResponseTest extends RobolectricTest {

   private MeetingInfoResponse meetingInfoResponse;

    @Before
    public void setUp() throws Exception {
        meetingInfoResponse = new MeetingInfoResponse(0L, 0);
    }

    @Test
    public void testGetId(){
        Random random = new Random();
        long expected = random.nextLong();
        meetingInfoResponse.setId(expected);
        long actual = meetingInfoResponse.getId();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetMeetingRoomGetResultId(){
        Random random = new Random();
        long expected = random.nextLong();
        meetingInfoResponse.setMeetingRoomGetResultId(expected);
        long actual = meetingInfoResponse.getMeetingRoomGetResultId();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetMeetingRoomGetResult(){
        String clientId = "1234567";
        meetingInfoResponse.setMeetingRoomGetResult(new MeetingRoomGetResult(clientId, null, null, null));
        MeetingRoomGetResult actual = meetingInfoResponse.getMeetingRoomGetResult();
        Assert.assertEquals(clientId, actual.getClientId());
    }
}
