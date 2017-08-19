package com.alesharik.localstorage.main.http.auth;

import com.alesharik.localstorage.main.data.AccessLevel;
import com.alesharik.localstorage.main.data.User;
import com.alesharik.webserver.api.collections.ConcurrentLiveHashMap;
import lombok.Data;

import javax.annotation.Nullable;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public final class TokenHolder {
    private static final long DAY_PERIOD = TimeUnit.DAYS.toMillis(1);
    private final ConcurrentLiveHashMap<UUID, LoginData> map = new ConcurrentLiveHashMap<>();

    public void addToken(UUID token, AccessLevel accessLevel, User user) {
        map.put(token, new LoginData(accessLevel, user), accessLevel == AccessLevel.CONFIDENTIAL ? 3600 : DAY_PERIOD);
    }

    public boolean isTokenValid(UUID token, AccessLevel accessLevel) {
        return map.containsKey(token) && map.get(token).getAccessLevel() == accessLevel;
    }

    @Nullable
    public User getUser(UUID token) {
        return map.containsKey(token) ? map.get(token).getUser() : null;
    }

    @Data
    private static class LoginData {
        private final AccessLevel accessLevel;
        private final User user;
    }
}
