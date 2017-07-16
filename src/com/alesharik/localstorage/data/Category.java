package com.alesharik.localstorage.data;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;

import java.util.UUID;

public class Category {
    private final UUID id;
    private final UUID recordId;
    private final UUID parent;

    private String name;
    private String desc;
    private JsonObject data;
}
