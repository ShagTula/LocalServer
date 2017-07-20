package com.alesharik.localstorage.data;

import com.alesharik.database.entity.Column;
import com.alesharik.database.entity.Entity;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@Entity(1)
public class Category {
    @Getter
    @Column(name = "id", primaryKey = true)
    private final UUID id;

    @Getter
    @Setter
    @Column(name = "name")
    private String name;

    @Getter
    @Setter
    @Column(name = "description")
    private String description;

    @Getter
    @Setter
    @Column(name = "public", hasIndex = true)
    private boolean isPublic;

    @Getter
    @Setter
    @Column(name = "data")
    private JsonObject data;

    @Getter
    @Setter
    @Column(name = "parent", nullable = true, hasIndex = true, foreignKey = true, refTable = DataManager.CATEGORY_TABLE_NAME)
    private UUID parent;

    @Getter
    @Setter
    @Column(name = "users", hasIndex = true, foreignKey = true, refTable = DataManager.USER_TABLE_NAME)
    private UUID[] users;

    @Getter
    @Setter
    @Column(name = "creator", hasIndex = true, foreignKey = true, refTable = DataManager.USER_TABLE_NAME)
    private UUID creator;

    public Category(UUID id) {
        this.id = id;
    }
}
