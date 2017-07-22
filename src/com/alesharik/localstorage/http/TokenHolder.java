package com.alesharik.localstorage.http;

import com.alesharik.localstorage.data.AccessLevel;
import com.alesharik.webserver.api.collections.ConcurrentLiveHashMap;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public final class TokenHolder {
    private static final long DAY_PERIOD = TimeUnit.DAYS.toMillis(1);
    private final ConcurrentLiveHashMap<UUID, AccessLevel> map = new ConcurrentLiveHashMap<>();

    public void addToken(UUID token, AccessLevel accessLevel) {
        map.put(token, accessLevel, accessLevel == AccessLevel.CONFIDENTIAL ? 3600 : DAY_PERIOD);
    }

    public boolean isTokenValid(UUID token, AccessLevel accessLevel) {
        return map.containsKey(token) && map.containsValue(accessLevel);
    }
}
