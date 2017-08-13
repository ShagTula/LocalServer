package com.alesharik.localstorage.data;

import com.alesharik.database.data.Table;
import com.alesharik.database.entity.Column;
import com.alesharik.database.entity.Destroyer;
import com.alesharik.database.entity.Entity;
import com.alesharik.database.entity.EntityManager;
import com.alesharik.database.entity.ForeignKey;
import com.alesharik.database.entity.Indexed;
import com.alesharik.database.entity.OverrideDomain;
import com.alesharik.database.entity.PrimaryKey;
import com.alesharik.database.entity.Unique;
import com.alesharik.localstorage.data.status.ChatStatus;
import com.alesharik.localstorage.data.status.InfoStatus;
import com.alesharik.localstorage.data.status.PrivateStatus;
import com.alesharik.webserver.api.StringCipher;
import com.google.gson.JsonObject;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.annotation.Nonnull;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.UUID;

@SuppressWarnings("NullableProblems")
@EqualsAndHashCode
@ToString
@Entity
public final class User {
    @Getter
    @Column("id")
    @PrimaryKey
    private final UUID id;

    @Getter
    @Setter
    @Column("login")
    @Indexed
    @Unique
    @OverrideDomain("varchar(30)")
    @Nonnull
    private String login;

    @Getter
    @Setter
    @Column("data")
    @Nonnull
    private JsonObject data;

    @Column("salt")
    @Nonnull
    private String salt;
    @Column("pass")
    @Nonnull
    private String passHash;

    @Getter
    @Setter
    @Column("chat_status")
    @ForeignKey("local_storage." + DataManager.CHAT_STATUS_TABLE_NAME)
    @Nonnull
    private UUID chatStatus;

    @Getter
    @Setter
    @Column("info_status")
    @ForeignKey("local_storage." + DataManager.INFO_STATUS_TABLE_NAME)
    @Nonnull
    private UUID infoStatus;

    @Getter
    @Setter
    @Column("private_status")
    @ForeignKey("local_storage." + DataManager.PRIVATE_STATUS_TABLE_NAME)
    @Nonnull
    private UUID privateStatus;

    public static User create(EntityManager<User> manager, String login, String password, EntityManager<ChatStatus> chatStatusManager, EntityManager<InfoStatus> infoStatusEntityManager, EntityManager<PrivateStatus> privateStatusEntityManager) {
        ChatStatus chatStatus = ChatStatus.create(chatStatusManager, login);
        InfoStatus infoStatus = InfoStatus.create(infoStatusEntityManager);
        PrivateStatus privateStatus = PrivateStatus.create(privateStatusEntityManager);
        User user = new User(UUID.randomUUID(), login, password);
        user.chatStatus = chatStatus.getId();
        user.infoStatus = infoStatus.getId();
        user.privateStatus = privateStatus.getId();
        user.data = new JsonObject();
        return user;
    }

    @Destroyer
    public void delete(Table<ChatStatus> chatStatusManager, Table<InfoStatus> infoStatusEntityManager, Table<PrivateStatus> privateStatusEntityManager) {
        chatStatusManager.selectByPrimaryKey(new ChatStatus(chatStatus)).delete();
        infoStatusEntityManager.selectByPrimaryKey(new InfoStatus(infoStatus)).delete();
        privateStatusEntityManager.selectByPrimaryKey(new PrivateStatus(privateStatus)).delete();
    }

    public User(UUID id, String login, String password) {
        this.id = id;
        this.login = login;
        byte[] src = generateSalt(24);
        this.salt = Base64.getEncoder().encodeToString(src);
        try {
            this.passHash = Base64.getEncoder().encodeToString(StringCipher.hashString(password, src, 512, 256));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public User(UUID id) {
        this.id = id;
    }

    public boolean passwordValid(String pass) {
        try {
            String current = Base64.getEncoder().encodeToString(StringCipher.hashString(pass, Base64.getDecoder().decode(salt), 512, 256));
            return passHash.equals(current);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean changePassword(String old, String newPass) {
        if(passwordValid(old)) {
            byte[] salt = generateSalt(24);
            this.salt = Base64.getEncoder().encodeToString(salt);
            try {
                this.passHash = Base64.getEncoder().encodeToString(StringCipher.hashString(newPass, salt, 512, 256));
                return true;
            } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
    }

    static byte[] generateSalt(int size) {
        SecureRandom secureRandom = new SecureRandom();
        byte[] ret = new byte[size];
        secureRandom.nextBytes(ret);
        return ret;
    }
}
