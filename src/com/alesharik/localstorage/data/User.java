package com.alesharik.localstorage.data;

import com.google.gson.JsonObject;
import one.nio.serial.Json;

import java.net.URI;
import java.net.URL;
import java.util.Date;
import java.util.UUID;

public class User {
    private final UUID id;

    private String login;

    private String hashedPass;
    private byte[] salt;

    private String firstName;
    private String lastName;
    private String patronymic;
    private String phone;
    private URL avatarUrl;
    private Date birthDate;
    private JsonObject data;
}
