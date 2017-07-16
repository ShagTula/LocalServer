package com.alesharik.localstorage.data;

import com.google.gson.JsonObject;

import java.util.UUID;

public class Data {
    private final UUID id;
    private final UUID categoryId;

    private String text;
    private JsonObject data;
}
