package com.alesharik.localstorage.main.data.status;

import com.alesharik.database.entity.Column;
import com.alesharik.database.entity.Destroyer;
import com.alesharik.database.entity.Entity;
import com.alesharik.database.entity.EntityManager;
import com.alesharik.database.entity.PrimaryKey;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

@SuppressWarnings("NullableProblems")
@EqualsAndHashCode
@AllArgsConstructor
@Entity
public final class PrivateStatus {
    @Getter
    @Column("id")
    @PrimaryKey
    private final UUID id;

    @Getter
    @Setter
    @Column("phone")
    @Nullable
    private String phone;

    @Getter
    @Setter
    @Column("data")
    @Nonnull
    private JsonObject data;

    public PrivateStatus(UUID id) {
        this.id = id;
    }

    public static PrivateStatus create(EntityManager<PrivateStatus> entityManager) {
        PrivateStatus privateStatus = new PrivateStatus(UUID.randomUUID());
        privateStatus.data = new JsonObject();
        return privateStatus;
    }

    @Destroyer
    public void delete() {}
}
