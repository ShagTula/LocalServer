package com.alesharik.localstorage.data.status;

import com.alesharik.database.entity.Column;
import com.alesharik.database.entity.Entity;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@Entity(1)
public final class PrivateStatus {
    @Getter
    @Column(name = "id", primaryKey = true)
    private final UUID id;

    @Getter
    @Setter
    @Column(name = "phone")
    private String phone;

    @Getter
    @Setter
    @Column(name = "data")
    private JsonObject data;

    public PrivateStatus(UUID id) {
        this.id = id;
    }
}