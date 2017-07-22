package com.alesharik.localstorage.data;

import com.alesharik.database.entity.Column;
import com.alesharik.database.entity.Entity;
import com.alesharik.webserver.api.StringCipher;
import com.google.gson.JsonObject;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.UUID;

@EqualsAndHashCode
@ToString
@Entity(1)
public final class User {
    @Getter
    @Column(name = "id", primaryKey = true, unique = true)
    private final UUID id;

    @Getter
    @Setter
    @Column(name = "login", hasIndex = true, unique = true)
    private String login;

    @Getter
    @Setter
    @Column(name = "data")
    private JsonObject data;

    @Column(name = "salt")
    private String salt;
    @Column(name = "pass")
    private String passHash;

    @Getter
    @Setter
    @Column(name = "chat_status", foreignKey = true, refTable = "local_storage." + DataManager.CHAT_STATUS_TABLE_NAME)
    private UUID chatStatus;

    @Getter
    @Setter
    @Column(name = "info_status", foreignKey = true, refTable = "local_storage." + DataManager.INFO_STATUS_TABLE_NAME)
    private UUID infoStatus;

    @Getter
    @Setter
    @Column(name = "private_status", foreignKey = true, refTable = "local_storage." + DataManager.PRIVATE_STATUS_TABLE_NAME)
    private UUID privateStatus;

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
