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
public class MultipartNote {
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
    @Column(name = "category", hasIndex = true, foreignKey = true, refTable = DataManager.CATEGORY_TABLE_NAME)
    private UUID category;

    @Getter
    @Setter
    @Column(name = "viewers", hasIndex = true, foreignKey = true, refTable = DataManager.USER_TABLE_NAME)
    private UUID[] viewers;

    @Getter
    @Setter
    @Column(name = "collaborators", hasIndex = true, foreignKey = true, refTable = DataManager.USER_TABLE_NAME)
    private UUID[] collaborators;

    @Getter
    @Setter
    @Column(name = "owner", hasIndex = true, foreignKey = true, refTable = DataManager.USER_TABLE_NAME)
    private UUID owner;

    @Getter
    @Setter
    @Column(name = "parts", hasIndex = true, foreignKey = true, refTable = DataManager.MULTIPART_NODE_PART_TABLE_NAME)
    private UUID[] parts;

    @Getter
    @Setter
    @Column(name = "history")
    private JsonObject[] history;

    public MultipartNote(UUID id) {
        this.id = id;
    }
}
