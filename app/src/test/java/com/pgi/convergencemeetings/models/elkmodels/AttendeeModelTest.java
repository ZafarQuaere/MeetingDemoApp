package com.pgi.convergencemeetings.models.elkmodels;

import com.pgi.convergencemeetings.RobolectricTest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class AttendeeModelTest extends RobolectricTest {

    private AttendeeModel attendeeModel;

    @Before
    public void setUp() throws Exception {
        attendeeModel = AttendeeModel.getInstance();
    }
    @Test
    public void getInstance() {
        AttendeeModel test = AttendeeModel.getInstance();
        Assert.assertNotNull(test);
    }

    @Test
    public void getUserid() {
        String expected = "testUserid";
        attendeeModel.setUserid(expected);
        String actual = attendeeModel.getUserid();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void setUserid() {
        String expected = "testUserid";
        attendeeModel.setUserid(expected);
        String actual = attendeeModel.getUserid();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void getFirstname() {
        String expected = "testFirstName";
        attendeeModel.setFirstname(expected);
        String actual = attendeeModel.getFirstname();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void setFirstname() {
        String expected = "testFirstName";
        attendeeModel.setFirstname(expected);
        String actual = attendeeModel.getFirstname();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void getLastname() {
        String expected = "testLastName";
        attendeeModel.setLastname(expected);
        String actual = attendeeModel.getLastname();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void setLastname() {
        String expected = "testLastName";
        attendeeModel.setLastname(expected);
        String actual = attendeeModel.getLastname();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void getEmail() {
        String expected = "testEmail";
        attendeeModel.setEmail(expected);
        String actual = attendeeModel.getEmail();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void setEmail() {
        String expected = "testEmail";
        attendeeModel.setEmail(expected);
        String actual = attendeeModel.getEmail();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void getOperatingsystem() {
        String expected = "testOperatingsystem";
        attendeeModel.setOperatingsystem(expected);
        String actual = attendeeModel.getOperatingsystem();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void setOperatingsystem() {
        String expected = "testOperatingsystem";
        attendeeModel.setOperatingsystem(expected);
        String actual = attendeeModel.getOperatingsystem();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void getOsVersion() {
        String expected = "testOsVersion";
        attendeeModel.setOsVersion(expected);
        String actual = attendeeModel.getOsVersion();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void setOsVersion() {
        String expected = "testOsVersion";
        attendeeModel.setOsVersion(expected);
        String actual = attendeeModel.getOsVersion();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void getAppType() {
        String expected = "testAppType";
        attendeeModel.setAppType(expected);
        String actual = attendeeModel.getAppType();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void setAppType() {
        String expected = "testAppType";
        attendeeModel.setAppType(expected);
        String actual = attendeeModel.getAppType();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void getDevice() {
        String expected = "testDevice";
        attendeeModel.setDevice(expected);
        String actual = attendeeModel.getDevice();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void setDevice() {
        String expected = "testDevice";
        attendeeModel.setDevice(expected);
        String actual = attendeeModel.getDevice();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void getRelaytype() {
        String expected = "testRelaytype";
        attendeeModel.setRelaytype(expected);
        String actual = attendeeModel.getRelaytype();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void setRelaytype() {
        String expected = "testRelaytype";
        attendeeModel.setRelaytype(expected);
        String actual = attendeeModel.getRelaytype();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void getConnectionprotocol() {
        String expected = "testConnectionProtocol";
        attendeeModel.setConnectionprotocol(expected);
        String actual = attendeeModel.getConnectionprotocol();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void setConnectionprotocol() {
        String expected = "testConnectionProtocol";
        attendeeModel.setConnectionprotocol(expected);
        String actual = attendeeModel.getConnectionprotocol();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void getAudioconnectiontype() {
        String expected = "testAudioConnectionType";
        attendeeModel.setAudioconnectiontype(expected);
        String actual = attendeeModel.getAudioconnectiontype();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void setAudioconnectiontype() {
        String expected = "testAudioConnectionType";
        attendeeModel.setAudioconnectiontype(expected);
        String actual = attendeeModel.getAudioconnectiontype();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void getDnis() {
        String expected = "testDnis";
        attendeeModel.setDnis(expected);
        String actual = attendeeModel.getDnis();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void setDnis() {
        String expected = "testDnis";
        attendeeModel.setDnis(expected);
        String actual = attendeeModel.getDnis();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void getCarrier() {
        String expected = "testCarrier";
        attendeeModel.setCarrier(expected);
        String actual = attendeeModel.getCarrier();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void setCarrier() {
        String expected = "testCarrier";
        attendeeModel.setCarrier(expected);
        String actual = attendeeModel.getCarrier();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void getAudiocodec() {
        String expected = "testAudiocodec";
        attendeeModel.setAudiocodec(expected);
        String actual = attendeeModel.getAudiocodec();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void setAudiocodec() {
        String expected = "testAudiocodec";
        attendeeModel.setAudiocodec(expected);
        String actual = attendeeModel.getAudiocodec();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void getRole() {
        String expected = "testRole";
        attendeeModel.setRole(expected);
        String actual = attendeeModel.getRole();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void setRole() {
        String expected = "testRole";
        attendeeModel.setRole(expected);
        String actual = attendeeModel.getRole();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void getParticipantid() {
        String expected = "testParticipantId";
        attendeeModel.setParticipantid(expected);
        String actual = attendeeModel.getParticipantid();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void setParticipantid() {
        String expected = "testParticipantId";
        attendeeModel.setParticipantid(expected);
        String actual = attendeeModel.getParticipantid();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void getCompanyId() {
        String expected = "testCompanyId";
        attendeeModel.setCompanyId(expected);
        String actual = attendeeModel.getCompanyId();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void setCompanyId() {
        String expected = "testCompanyId";
        attendeeModel.setCompanyId(expected);
        String actual = attendeeModel.getCompanyId();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void getCallId() {
        String expected = "testCallId";
        attendeeModel.setCallId(expected);
        String actual = attendeeModel.getCallId();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void setCallId() {
        String expected = "testCallId";
        attendeeModel.setCallId(expected);
        String actual = attendeeModel.getCallId();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void resetAttendeeInfo() {
        attendeeModel.resetAttendeeInfo();
        String expected = "";
        String actual = attendeeModel.getUserid();
        Assert.assertEquals(expected, actual);
        actual = attendeeModel.getFirstname();
        Assert.assertEquals(expected, actual);
        actual = attendeeModel.getLastname();
        Assert.assertEquals(expected, actual);
        actual = attendeeModel.getEmail();
        Assert.assertEquals(expected, actual);
        actual = attendeeModel.getDnis();
        Assert.assertEquals(expected, actual);
        actual = attendeeModel.getRole();
        Assert.assertEquals(expected, actual);
        actual = attendeeModel.getAudioconnectiontype();
        Assert.assertEquals(expected, actual);

    }
}