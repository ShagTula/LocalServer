package com.alesharik.localstorage.data;

import com.google.gson.JsonObject;

import java.util.UUID;

public class Record {
    private final UUID id;
    private final UUID userId;

    private String name;
    private String desc;

    private JsonObject data;
}
