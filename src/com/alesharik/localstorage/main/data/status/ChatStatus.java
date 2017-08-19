package com.alesharik.localstorage.main.data.status;

import com.alesharik.database.entity.Column;
import com.alesharik.database.entity.Creator;
import com.alesharik.database.entity.Destroyer;
import com.alesharik.database.entity.Entity;
import com.alesharik.database.entity.EntityManager;
import com.alesharik.database.entity.Indexed;
import com.alesharik.database.entity.OverrideDomain;
import com.alesharik.database.entity.PrimaryKey;
import com.google.gson.JsonObject;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.annotation.Nonnull;
import java.util.UUID;

@SuppressWarnings("NullableProblems")
@EqualsAndHashCode
@ToString
@Entity
public final class ChatStatus {
    public static final String NONE_AVATAR = "none.png";

    @Getter
    @Column("id")
    @PrimaryKey
    private final UUID id;

    @Getter
    @Setter
    @Column("nickname")
    @OverrideDomain("varchar(30)")
    @Indexed
    @Nonnull
    private String nickName;

    @Getter
    @Setter
    @Column("avatar")
    @Nonnull
    private String avatarUrl;

    @Getter
    @Setter
    @Column("data")
    @Nonnull
    private JsonObject data;

    @Column("status")
    @Nonnull
    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.PRIVATE)
    private Integer status;

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

    @Creator
    public static ChatStatus create(EntityManager<ChatStatus> manager, String nick) {
        ChatStatus chatStatus = new ChatStatus(UUID.randomUUID());
        chatStatus.nickName = nick;
        chatStatus.avatarUrl = NONE_AVATAR;
        chatStatus.data = new JsonObject();
        chatStatus.status = OnlineStatus.OFFLINE.getState();
        return chatStatus;
    }

    @Destroyer
    public void delete() {}

    public OnlineStatus getOnlineStatus() {
        return OnlineStatus.forState(getStatus());
    }

    public void setOnlineStatus(OnlineStatus status) {
        setStatus(status.getState());
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
