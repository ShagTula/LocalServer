package com.alesharik.localstorage.data;

import lombok.Getter;

public enum AccessLevel {
    PUBLIC(1),
    PRIVATE(2),
    CONFIDENTIAL(3);

    @Getter
    private final int key;

    AccessLevel(int key) {
        this.key = key;
    }

    public static AccessLevel forKey(int key) {
        switch (key) {
            case 1:
                return PUBLIC;
            case 2:
                return PRIVATE;
            case 3:
                return CONFIDENTIAL;
            default:
                return null;
        }
    }
}
