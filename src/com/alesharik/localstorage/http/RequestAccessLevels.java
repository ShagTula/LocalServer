package com.alesharik.localstorage.http;

import com.alesharik.localstorage.data.AccessLevel;

public class RequestAccessLevels {
    public static AccessLevel forPath(String path) {
        return AccessLevel.PUBLIC;
    }
}
