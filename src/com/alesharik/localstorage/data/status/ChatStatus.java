package com.alesharik.localstorage.data.status;

import com.alesharik.database.entity.Column;
import com.alesharik.database.entity.Entity;
import com.google.gson.JsonObject;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@EqualsAndHashCode
@ToString
@Entity(1)
public final class ChatStatus {
    @Getter
    @Column(name = "id", primaryKey = true, unique = true)
    private final UUID id;

    @Getter
    @Setter
    @Column(name = "nickname", hasIndex = true)
    private String nickName;

    @Getter
    @Setter
    @Column(name = "avatar")
    private String avatarUrl;

    @Getter
    @Setter
    @Column(name = "data")
    private JsonObject data;

    @Column(name = "status")
    private int status;

    public ChatStatus(UUID id) {
        this.id = id;
    }

    public ChatStatus(UUID id, String nickName, String avatarUrl, JsonObject data, OnlineStatus status) {
        this.id = id;
        this.nickName = nickName;
        this.avatarUrl = avatarUrl;
        this.data = data;
        this.status = status.getState();
    }

    public OnlineStatus getStatus() {
        return OnlineStatus.forState(status);
    }

    public void setOnlineStatus(OnlineStatus status) {
        this.status = status.getState();
    }

    public enum OnlineStatus {
        OFFLINE(0),
        ONLINE(1),
        DO_NOT_DISTURB(2),
        NOT_ACTIVE(3);

        @Getter(AccessLevel.PUBLIC)
        private final int state;

        OnlineStatus(int state) {
            this.state = state;
        }

        public static OnlineStatus forState(int state) {
            switch (state) {
                case 0:
                    return OFFLINE;
                case 1:
                    return ONLINE;
                case 2:
                    return DO_NOT_DISTURB;
                case 3:
                    return NOT_ACTIVE;
                default:
                    return OFFLINE;
            }
        }
    }
}
