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
public final class MultipartNotePart {
    @Getter
    @Column(name = "id", primaryKey = true)
    private final UUID id;

    @Getter
    @Setter
    @Column(name = "data")
    private JsonObject data;

    public MultipartNotePart(UUID id) {
        this.id = id;
    }
}
