package com.alesharik.localstorage.data;

import com.alesharik.database.entity.Column;
import com.alesharik.database.entity.Entity;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@EqualsAndHashCode
@ToString
@AllArgsConstructor
@Entity(1)
public final class MultipartNote {
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
    @Column(name = "category", hasIndex = true, foreignKey = true, refTable = "local_storage." + DataManager.CATEGORY_TABLE_NAME)
    private UUID category;

    @Getter
    @Setter
    @Column(name = "viewers", hasIndex = true)
    private UUID[] viewers;

    @Getter
    @Setter
    @Column(name = "collaborators")
    private UUID[] collaborators;

    @Getter
    @Setter
    @Column(name = "owner", hasIndex = true, foreignKey = true, refTable = "local_storage." + DataManager.USER_TABLE_NAME)
    private UUID owner;

    @Getter
    @Setter
    @Column(name = "parts", hasIndex = true)
    private UUID[] parts;

    @Getter
    @Setter
    @Column(name = "history")
    private JsonObject[] history;

    public MultipartNote(UUID id) {
        this.id = id;
    }
}
