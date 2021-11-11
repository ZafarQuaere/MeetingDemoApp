package com.pgi.convergencemeetings.models.meetinginfomodels;

import com.pgi.convergencemeetings.RobolectricTest;
import com.pgi.convergencemeetings.models.Attendee;
import com.pgi.convergencemeetings.models.AudioDetail;
import com.pgi.convergencemeetings.models.ConferenceOption;
import com.pgi.convergencemeetings.models.Errors;
import com.pgi.convergencemeetings.models.MeetingRoomDetail;
import com.pgi.convergencemeetings.models.MeetingRoomGetResult;
import com.pgi.convergencemeetings.models.MeetingRoomUrls;
import com.pgi.convergencemeetings.models.Package;
import com.pgi.convergencemeetings.models.Presenter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by amit1829 on 8/22/2017.
 */

public class MeetingRoomGetResultTest extends RobolectricTest {

    private MeetingRoomGetResult meetingRoomGetResult;

    @Before
    public void setUp() throws Exception {
        meetingRoomGetResult = new MeetingRoomGetResult(0L, null, null, null, null, 0, null,
                null, null, null, null, 0, null, 0, null, null, null, null, true, null, 0, null, null);
    }

    @Test
    public void testGetId() {
        Random random = new Random();
        long expected = random.nextLong();
        meetingRoomGetResult.setId(expected);
        long actual = meetingRoomGetResult.getId();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetMeetingRoomUrlsId() {
        String roomUrlId = "1234567";
        meetingRoomGetResult.setMeetingRoomUrlsId(roomUrlId);
        String actual = meetingRoomGetResult.getMeetingRoomUrlsId();
        Assert.assertEquals(roomUrlId, actual);
    }

    @Test
    public void testGetAudioDetailId() {
        String audioDetaiId = "1234567";
        meetingRoomGetResult.setAudioDetailId(audioDetaiId);
        String actual = meetingRoomGetResult.getAudioDetailId();
        Assert.assertEquals(audioDetaiId, actual);
    }

    @Test
    public void testGetMeetingRoomDetailId() {
        String meetingRoomDetailId = "1234567";
        meetingRoomGetResult.setMeetingRoomDetailId(meetingRoomDetailId);
        String actual = meetingRoomGetResult.getMeetingRoomDetailId();
        Assert.assertEquals(meetingRoomDetailId, actual);
    }

    @Test
    public void testGetCorrelationId() {
        String correlationId = "1234567";
        meetingRoomGetResult.setCorrelationId(correlationId);
        String actual = meetingRoomGetResult.getCorrelationId();
        Assert.assertEquals(correlationId, actual);
    }

    @Test
    public void testGetMessageId() {
        String messageId = "1234567";
        meetingRoomGetResult.setMessageId(messageId);
        String actual = meetingRoomGetResult.getMessageId();
        Assert.assertEquals(messageId, actual);
    }

    @Test
    public void testGetServerDateTime() {
        String expected = "1234567";
        meetingRoomGetResult.setServerDateTime(expected);
        String actual = meetingRoomGetResult.getServerDateTime();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetAttendeeStartPermission() {
        String expected = "1234567";
        meetingRoomGetResult.setAttendeeStartPermission(expected);
        String actual = meetingRoomGetResult.getAttendeeStartPermission();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetAttendeeWebPassCode() {
        String expected = "1234567";
        meetingRoomGetResult.setAttendeeWebPassCode(expected);
        String actual = meetingRoomGetResult.getAttendeeWebPassCode();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetClientId() {
        String expected = "1234567";
        meetingRoomGetResult.setClientId(expected);
        String actual = meetingRoomGetResult.getClientId();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetHubName() {
        String expected = "1234567";
        meetingRoomGetResult.setHubName(expected);
        String actual = meetingRoomGetResult.getHubName();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetPresenterStartPermission() {
        String expected = "1234567";
        meetingRoomGetResult.setPresenterStartPermission(expected);
        String actual = meetingRoomGetResult.getPresenterStartPermission();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetPtok() {
        String expected = "1234567";
        meetingRoomGetResult.setPtok(expected);
        String actual = meetingRoomGetResult.getPtok();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetStatus() {
        String expected = "1234567";
        meetingRoomGetResult.setStatus(expected);
        String actual = meetingRoomGetResult.getStatus();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetWebMeetingServer() {
        String expected = "1234567";
        meetingRoomGetResult.setWebMeetingServer(expected);
        String actual = meetingRoomGetResult.getWebMeetingServer();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetCustomAudioData() {
        String expected = "1234567";
        meetingRoomGetResult.setCustomAudioData(expected);
        String actual = meetingRoomGetResult.getCustomAudioData();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetDeleteReason() {
        String expected = "1234567";
        meetingRoomGetResult.setDeleteReason(expected);
        String actual = meetingRoomGetResult.getDeleteReason();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetDeletedDateTime() {
        String expected = "1234567";
        meetingRoomGetResult.setDeletedDateTime(expected);
        String actual = meetingRoomGetResult.getDeletedDateTime();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetHubId() {
        Random random = new Random();
        int expected = random.nextInt();
        meetingRoomGetResult.setHubId(expected);
        int actual = meetingRoomGetResult.getHubId();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetMeetingRoomId() {
        Random random = new Random();
        int expected = random.nextInt();
        meetingRoomGetResult.setMeetingRoomId(expected);
        int actual = meetingRoomGetResult.getMeetingRoomId();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetHubBrandId() {
        Random random = new Random();
        int expected = random.nextInt();
        meetingRoomGetResult.setHubBrandId(expected);
        int actual = meetingRoomGetResult.getHubBrandId();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetExecutionTime() {
        Random random = new Random();
        int expected = random.nextInt();
        meetingRoomGetResult.setExecutionTime(expected);
        int actual = meetingRoomGetResult.getExecutionTime();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetParticipantAnonymity() {
        meetingRoomGetResult.setParticipantAnonymity(false);
        boolean actual = meetingRoomGetResult.getParticipantAnonymity();
        Assert.assertFalse(actual);
    }

    @Test
    public void testGetErrors() {
        Random random = new Random();
        long expectedId = random.nextLong();
        List<Errors> expected = new ArrayList<>();
        expected.add(new Errors(expectedId, null));
        meetingRoomGetResult.setErrors(expected);
        Errors actual = meetingRoomGetResult.getErrors().get(0);
        Assert.assertEquals(expectedId, (long) actual.getId());
    }

    @Test
    public void testGetConferenceOptions() {
        Random random = new Random();
        long expectedId = random.nextLong();
        List<ConferenceOption> expected = new ArrayList<>();
        expected.add(new ConferenceOption(expectedId, null, false, null));
        meetingRoomGetResult.setConferenceOptions(expected);
        ConferenceOption actual = meetingRoomGetResult.getConferenceOptions().get(0);
        Assert.assertEquals(expectedId, (long) actual.getId());
    }

    @Test
    public void testGetAttendee() {
        Random random = new Random();
        long expectedId = random.nextLong();
        List<Attendee> expected = new ArrayList<>();
        Attendee attendee = new Attendee();
        attendee.setId(expectedId);
        expected.add(attendee);
        meetingRoomGetResult.setAttendees(expected);
        Attendee actual = meetingRoomGetResult.getAttendees().get(0);
        Assert.assertEquals(expectedId, (long) actual.getId());
    }

    @Test
    public void testGetPackages() {
        Random random = new Random();
        long expectedId = random.nextLong();
        List<Package> expected = new ArrayList<>();
        Package aPackage = new Package();
        aPackage.setId(expectedId);
        expected.add(aPackage);
        meetingRoomGetResult.setPackages(expected);
        Package actual = meetingRoomGetResult.getPackages().get(0);
        Assert.assertEquals(expectedId, (long) actual.getId());
    }

    @Test
    public void testGetPresenters() {
        Random random = new Random();
        long expectedId = random.nextLong();
        List<Presenter> expected = new ArrayList<>();
        Presenter presenter = new Presenter();
        presenter.setId(expectedId);
        expected.add(presenter);
        meetingRoomGetResult.setPresenters(expected);
        Presenter actual = meetingRoomGetResult.getPresenters().get(0);
        Assert.assertEquals(expectedId, (long) actual.getId());
    }

    @Test
    public void testGetMeetingRoomUrls() {
        Random random = new Random();
        long expectedId = random.nextLong();
        MeetingRoomUrls expected = new MeetingRoomUrls();
        expected.setId(expectedId);
        meetingRoomGetResult.setMeetingRoomUrls(expected);
        Assert.assertEquals(expectedId, (long) meetingRoomGetResult.getMeetingRoomUrls().getId());
    }

    @Test
    public void testGetMeetingRoomDetail() {
        Random random = new Random();
        long expectedId = random.nextLong();
        MeetingRoomDetail expected = new MeetingRoomDetail();
        expected.setId(expectedId);
        meetingRoomGetResult.setMeetingRoomDetail(expected);
        Assert.assertEquals(expectedId, (long) meetingRoomGetResult.getMeetingRoomDetail().getId());
    }

    @Test
    public void testGetAudioDetail() {
        Random random = new Random();
        long expectedId = random.nextLong();
        AudioDetail expected = new AudioDetail();
        expected.setId(expectedId);
        meetingRoomGetResult.setAudioDetail(expected);
        Assert.assertEquals(expectedId, (long) meetingRoomGetResult.getAudioDetail().getId());
    }
}
