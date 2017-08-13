package com.alesharik.localstorage.data.status;

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
import lombok.ToString;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.sql.Date;
import java.util.UUID;

@SuppressWarnings("NullableProblems")
@EqualsAndHashCode
@ToString
@AllArgsConstructor
@Entity
public final class InfoStatus {
    @Getter
    @Column("id")
    @PrimaryKey
    private final UUID id;

    @Getter
    @Setter
    @Column("first_name")
    @Nullable
    private String firstName;

    @Getter
    @Setter
    @Column("last_name")
    @Nullable
    private String lastName;

    @Getter
    @Setter
    @Column("patronymic")
    @Nullable
    private String patronymic;

    @Getter
    @Setter
    @Column("birthday")
    @Nullable
    private Date birthday;

    @Getter
    @Setter
    @Column("data")
    @Nonnull
    private JsonObject data;

    public InfoStatus(UUID id) {
        this.id = id;
    }

    public static InfoStatus create(EntityManager<InfoStatus> entityManager) {
        InfoStatus infoStatus = new InfoStatus(UUID.randomUUID());
        infoStatus.data = new JsonObject();
        return infoStatus;
    }

    @Destroyer
    public void delete() {}
}
