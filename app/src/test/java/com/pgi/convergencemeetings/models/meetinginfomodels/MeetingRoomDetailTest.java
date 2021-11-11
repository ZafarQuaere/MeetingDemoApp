package com.pgi.convergencemeetings.models.meetinginfomodels;

import com.pgi.convergencemeetings.RobolectricTest;
import com.pgi.convergencemeetings.models.MeetingRoomDetail;
import com.pgi.convergencemeetings.models.WebConferenceOption;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by amit1829 on 8/22/2017.
 */

public class MeetingRoomDetailTest extends RobolectricTest {

    private MeetingRoomDetail meetingRoomDetail;

    @Before
    public void setUp() throws Exception {
        meetingRoomDetail = new MeetingRoomDetail(0L, null, true, 0, null, true, null, null, null, null,
                null, null, 0, 0, null, null, null, null, null, null, true, null, null, null, false,
                null, null, false, null, true);
    }

    @Test
    public void testGetId() {
        Random random = new Random();
        long expected = random.nextLong();
        meetingRoomDetail.setId(expected);
        long actual = meetingRoomDetail.getId();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetMeetingRoomDetailId() {
        String expected = "123456";
        meetingRoomDetail.setMeetingRoomDetailId(expected);
        String actual = meetingRoomDetail.getMeetingRoomDetailId();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetAnonymousEvaluation() {
        meetingRoomDetail.setAnonymousEvaluation(true);
        boolean actual = meetingRoomDetail.getAnonymousEvaluation();
        Assert.assertTrue(actual);
    }

    @Test
    public void testGetAudioConferenceId() {
        Random random = new Random();
        int expected = random.nextInt();
        meetingRoomDetail.setAudioConferenceId(expected);
        int actual = meetingRoomDetail.getAudioConferenceId();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetAudioProviderType() {
        String expected = "providerType";
        meetingRoomDetail.setAudioProviderType(expected);
        String actual = meetingRoomDetail.getAudioProviderType();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetAutoAcceptRegistrants() {
        meetingRoomDetail.setAutoAcceptRegistrants(true);
        boolean actual = meetingRoomDetail.getAutoAcceptRegistrants();
        Assert.assertTrue(actual);
    }

    @Test
    public void testGetAvailability() {
        String expected = "Available";
        meetingRoomDetail.setAvailability(expected);
        String actual = meetingRoomDetail.getAvailability();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetCategory() {
        String expected = "Category";
        meetingRoomDetail.setCategory(expected);
        String actual = meetingRoomDetail.getCategory();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetConferenceDescription() {
        String expected = "ConferenceDescription";
        meetingRoomDetail.setConferenceDescription(expected);
        String actual = meetingRoomDetail.getConferenceDescription();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetConferenceTitle() {
        String expected = "ConferenceTitle";
        meetingRoomDetail.setConferenceTitle(expected);
        String actual = meetingRoomDetail.getConferenceTitle();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetInvitationMessage() {
        String expected = "InvitationMessage";
        meetingRoomDetail.setInvitationMessage(expected);
        String actual = meetingRoomDetail.getInvitationMessage();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetInvitationSubject() {
        String expected = "InvitationSubject";
        meetingRoomDetail.setInvitationSubject(expected);
        String actual = meetingRoomDetail.getInvitationSubject();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetMaxParticipants() {
        Random random = new Random();
        int expected = random.nextInt();
        meetingRoomDetail.setMaxParticipants(expected);
        int actual = meetingRoomDetail.getMaxParticipants();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetMaxRegistrants() {
        Random random = new Random();
        int expected = random.nextInt();
        meetingRoomDetail.setMaxRegistrants(expected);
        int actual = meetingRoomDetail.getMaxRegistrants();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetModeratorEmail() {
        String expected = "ModeratorEmail";
        meetingRoomDetail.setModeratorEmail(expected);
        String actual = meetingRoomDetail.getModeratorEmail();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetModeratorInvitationMessage() {
        String expected = "ModeratorInvitationMessage";
        meetingRoomDetail.setModeratorInvitationMessage(expected);
        String actual = meetingRoomDetail.getModeratorInvitationMessage();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetModeratorInvitationSubject() {
        String expected = "ModeratorInvitationSubject";
        meetingRoomDetail.setModeratorInvitationSubject(expected);
        String actual = meetingRoomDetail.getModeratorInvitationSubject();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetModeratorName() {
        String expected = "ModeratorName";
        meetingRoomDetail.setModeratorName(expected);
        String actual = meetingRoomDetail.getModeratorName();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetModeratorSecurityCode() {
        String expected = "ModeratorSecurityCode";
        meetingRoomDetail.setModeratorSecurityCode(expected);
        String actual = meetingRoomDetail.getModeratorSecurityCode();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetPONumber() {
        String expected = "PONumber";
        meetingRoomDetail.setPONumber(expected);
        String actual = meetingRoomDetail.getPONumber();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetRecordAllEvents() {
        meetingRoomDetail.setRecordAllEvents(true);
        boolean actual = meetingRoomDetail.getRecordAllEvents();
        Assert.assertTrue(actual);
    }

    @Test
    public void testGetRegistrationClosedDateTime() {
        String expected = "RegistrationClosedDateTime";
        meetingRoomDetail.setRegistrationClosedDateTime(expected);
        String actual = meetingRoomDetail.getRegistrationClosedDateTime();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetRoomName() {
        String expected = "RoomName";
        meetingRoomDetail.setRoomName(expected);
        String actual = meetingRoomDetail.getRoomName();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetSecurityCode() {
        String expected = "SecurityCode";
        meetingRoomDetail.setSecurityCode(expected);
        String actual = meetingRoomDetail.getSecurityCode();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetSendEmailOnRegistration() {
        meetingRoomDetail.setSendEmailOnRegistration(true);
        boolean actual = meetingRoomDetail.getSendEmailOnRegistration();
        Assert.assertTrue(actual);
    }

    @Test
    public void testGetTimeZone() {
        String expected = "TimeZone";
        meetingRoomDetail.setTimeZone(expected);
        String actual = meetingRoomDetail.getTimeZone();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetWebProviderType() {
        String expected = "WebProviderType";
        meetingRoomDetail.setWebProviderType(expected);
        String actual = meetingRoomDetail.getWebProviderType();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetDefaultLanguage() {
        String expected = "DefaultLanguage";
        meetingRoomDetail.setDefaultLanguage(expected);
        String actual = meetingRoomDetail.getDefaultLanguage();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetSecure() {
        meetingRoomDetail.setSecure(true);
        boolean actual = meetingRoomDetail.getSecure();
        Assert.assertTrue(actual);
    }

    @Test
    public void testGetUseHtml5() {
        meetingRoomDetail.setUseHtml5(true);
        boolean actual = meetingRoomDetail.getUseHtml5();
        Assert.assertTrue(actual);
    }

    @Test
    public void testGetWebConferenceOptions() {
        Random random = new Random();
        long expectedId = random.nextLong();
        List<WebConferenceOption> expected = new ArrayList<>();
        expected.add(new WebConferenceOption(expectedId, null, null, true, true));
        meetingRoomDetail.setWebConferenceOptions(expected);
        WebConferenceOption actual = meetingRoomDetail.getWebConferenceOptions().get(0);
        Assert.assertEquals(expectedId, (long) actual.getId());
    }
}
