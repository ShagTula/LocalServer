package com.alesharik.localstorage.data;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UserTest {
    @Test
    public void passwordTest() throws Exception {
        User user = new User(UUID.randomUUID(), "admin", "admin");
        assertTrue(user.passwordValid("admin"));
        assertFalse(user.passwordValid("user"));

        assertFalse(user.changePassword("user", "admin"));
        assertTrue(user.changePassword("admin", "user"));

        assertFalse(user.passwordValid("admin"));
        assertTrue(user.passwordValid("user"));
    }

    @Test
    public void generateSalt() throws Exception {
        List<byte[]> arrTest = new ArrayList<>();
        for(int i = 0; i < 1000; i++) {
            byte[] gen = User.generateSalt(24);
            assertFalse(arrTest.contains(gen));
            arrTest.add(gen);
        }
    }

}