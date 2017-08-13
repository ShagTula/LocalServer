package com.alesharik.localstorage.data;

import com.alesharik.database.entity.Column;
import com.alesharik.database.entity.Entity;
import com.alesharik.database.entity.PrimaryKey;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.annotation.Nonnull;
import java.util.UUID;

@SuppressWarnings("NullableProblems")
@EqualsAndHashCode
@ToString
@AllArgsConstructor
@Entity
public abstract class MultipartNotePart {
    @Getter
    @Column("id")
    @PrimaryKey
    private final UUID id;

    @Getter
    @Setter
    @Column("data")
    @Nonnull
    private JsonObject data;

    public MultipartNotePart(UUID id) {
        this.id = id;
    }
}
