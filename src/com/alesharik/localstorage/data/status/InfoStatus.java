package com.alesharik.localstorage.data.status;

import com.alesharik.database.entity.Column;
import com.alesharik.database.entity.Entity;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
import java.util.UUID;

@AllArgsConstructor
@Entity(1)
public final class InfoStatus {
    @Getter
    @Column(name = "id", primaryKey = true)
    private final UUID id;

    @Getter
    @Setter
    @Column(name = "first_name")
    private String firstName;

    @Getter
    @Setter
    @Column(name = "last_name")
    private String lastName;

    @Getter
    @Setter
    @Column(name = "patronymic")
    private String patronymic;

    @Getter
    @Setter
    @Column(name = "birthday")
    private Date birthday;

    @Getter
    @Setter
    @Column(name = "data")
    private JsonObject data;

    public InfoStatus(UUID id) {
        this.id = id;
    }
}
