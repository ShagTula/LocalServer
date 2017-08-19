package com.alesharik.localstorage.main.data.status;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ChatStatusTest {
    @Test
    public void parseStatusTest() throws Exception {
        for(ChatStatus.OnlineStatus onlineStatus : ChatStatus.OnlineStatus.values()) {
            assertEquals(onlineStatus, ChatStatus.OnlineStatus.forState(onlineStatus.getState()));
        }
        assertEquals(ChatStatus.OnlineStatus.OFFLINE, ChatStatus.OnlineStatus.forState(4));
    }
}