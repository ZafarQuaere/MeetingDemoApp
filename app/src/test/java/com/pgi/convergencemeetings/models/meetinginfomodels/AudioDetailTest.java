package com.pgi.convergencemeetings.models.meetinginfomodels;

import com.pgi.convergencemeetings.RobolectricTest;
import com.pgi.convergencemeetings.models.AudioDetail;
import com.pgi.convergencemeetings.models.PhoneInformation;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by amit1829 on 8/22/2017.
 */

public class AudioDetailTest extends RobolectricTest {

    private AudioDetail audioDetail;

    @Before
    public void setUp() throws Exception {
        audioDetail = new AudioDetail(0L, null, null,
                null, null, null, null, null, null, null);
    }

    @Test
    public void testGetId() {
        Random random = new Random();
        long expected = random.nextLong();
        audioDetail.setId(expected);
        long actual = audioDetail.getId();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetAudioDetailId() {
        String expected = "attendeesId";
        audioDetail.setAudioDetailId(expected);
        String actual = audioDetail.getAudioDetailId();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetConfId() {
        String expected = "conferenceID";
        audioDetail.setConfId(expected);
        String actual = audioDetail.getConfId();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetConferenceName() {
        String expected = "conferenceName";
        audioDetail.setConferenceName(expected);
        String actual = audioDetail.getConferenceName();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetListenPassCode() {
        String expected = "23424234";
        audioDetail.setListenPassCode(expected);
        String actual = audioDetail.getListenPassCode();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetModeratorPassCode() {
        String expected = "23424234";
        audioDetail.setModeratorPassCode(expected);
        String actual = audioDetail.getModeratorPassCode();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetParticipantPassCode() {
        String expected = "234242343242";
        audioDetail.setParticipantPassCode(expected);
        String actual = audioDetail.getParticipantPassCode();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetBridgeOptions() {
        List<String> expected = new ArrayList<>();
        expected.add("BridgeOptions");
        audioDetail.setBridgeOptions(expected);
        String actual = audioDetail.getBridgeOptions().get(0);
        Assert.assertEquals(expected.get(0), actual);
    }

    @Test
    public void testGetPhoneInformation() {
        Random random = new Random();
        long expectedID = random.nextLong();
        List<PhoneInformation> expected = new ArrayList<>();
        expected.add(new PhoneInformation(expectedID, null, null, null, null, null));
        audioDetail.setPhoneInformation(expected);
        PhoneInformation actual = audioDetail.getPhoneInformation().get(0);
        Assert.assertEquals(expectedID, (long) actual.getId());
    }

    @Test
    public void testGetReservationOptions() {
        Random random = new Random();
        long expectedID = random.nextLong();
        List<String> expected = new ArrayList<>();
        expected.add(""+expectedID);
        audioDetail.setReservationOptions(expected);
        String actual = audioDetail.getReservationOptions().get(0);
        Assert.assertEquals(""+expectedID, actual);
    }
}
