package com.pgi.convergencemeetings.models.meetinginfomodels;

import com.pgi.convergencemeetings.RobolectricTest;
import com.pgi.convergencemeetings.models.MeetingRoomUrls;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

/**
 * Created by amit1829 on 8/22/2017.
 */

public class MeetingRoomUrlsTest extends RobolectricTest {
    private MeetingRoomUrls meetingRoomUrls;

    @Before
    public void setUp() throws Exception {
        meetingRoomUrls = new MeetingRoomUrls(0L, null, null, null, null, null, null, null);
    }

    @Test
    public void testGetId() {
        Random random = new Random();
        long expected = random.nextLong();
        meetingRoomUrls.setId(expected);
        long actual = meetingRoomUrls.getId();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetMeetingRoomUrlsId() {
        String roomUrlId = "1234567";
        meetingRoomUrls.setMeetingRoomUrlsId(roomUrlId);
        String actual = meetingRoomUrls.getMeetingRoomUrlsId();
        Assert.assertEquals(roomUrlId, actual);
    }

    @Test
    public void testGetAttendeeEvaluateUrl() {
        String expected = "1234567";
        meetingRoomUrls.setAttendeeEvaluateUrl(expected);
        String actual = meetingRoomUrls.getAttendeeEvaluateUrl();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetAttendeeJoinUrl() {
        String expected = "1234567";
        meetingRoomUrls.setAttendeeJoinUrl(expected);
        String actual = meetingRoomUrls.getAttendeeJoinUrl();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetAttendeeRegisterUrl() {
        String expected = "1234567";
        meetingRoomUrls.setAttendeeRegisterUrl(expected);
        String actual = meetingRoomUrls.getAttendeeRegisterUrl();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetFileDownloadUrl() {
        String expected = "1234567";
        meetingRoomUrls.setFileDownloadUrl(expected);
        String actual = meetingRoomUrls.getFileDownloadUrl();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetPresenterJoinUrl() {
        String expected = "1234567";
        meetingRoomUrls.setPresenterJoinUrl(expected);
        String actual = meetingRoomUrls.getPresenterJoinUrl();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetEndOfMeetingUrl() {
        String expected = "1234567";
        meetingRoomUrls.setEndOfMeetingUrl(expected);
        String actual = meetingRoomUrls.getEndOfMeetingUrl();
        Assert.assertEquals(expected, actual);
    }
}
