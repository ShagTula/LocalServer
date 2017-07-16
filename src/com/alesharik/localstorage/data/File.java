package com.alesharik.localstorage.data;

import com.google.gson.JsonObject;

import java.util.UUID;

public class File {
    private final UUID id;
    private final UUID dataId;

    private String fileName;
    private String filePath;
    private JsonObject data;
}
