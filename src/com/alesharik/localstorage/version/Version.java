package com.alesharik.localstorage.version;

import com.alesharik.webserver.logger.Prefixes;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import javax.annotation.MatchesPattern;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.io.File;
import java.io.Serializable;
import java.util.regex.Pattern;

/**
 * This class represents client application version
 */
@SuppressWarnings("WeakerAccess")
@Immutable
@EqualsAndHashCode
@AllArgsConstructor
@ToString
@Getter
@Prefixes({"[LocalStorage]", "[Version]"})
public final class Version implements Serializable, Comparable<Version> {
    private static final Pattern VERSION_PATTERN = Pattern.compile("\\d-\\d-\\d_[RSUN]");

    private final int majorVersion;
    private final int minorVersion;
    private final int snapshotVersion;
    private final Prefix prefix;
    private final File file;

    public static Version fromVersionString(@MatchesPattern("\\d-\\d-\\d_[RSUN]") @Nonnull String name, @Nonnull File file) {
        if(!VERSION_PATTERN.matcher(name).matches())
            throw new IllegalArgumentException();

        String[] parts = name.split("[-_]");
        return new Version(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Prefix.parse(parts[3].charAt(0)), file);
    }

    /**
     * Not compare prefixes
     */
    @Override
    public int compareTo(@Nonnull Version o) {
        if(majorVersion < o.majorVersion)
            return -1;
        else if(majorVersion > o.majorVersion)
            return 1;
        else {
            if(minorVersion < o.minorVersion)
                return -1;
            else if(minorVersion > o.minorVersion)
                return 1;
            else
                return Integer.compare(snapshotVersion, o.snapshotVersion);
        }
    }

    public void delete() {
        if(!file.delete())
            System.err.println("Can't delete file " + file.toString() + " !");
    }

    public String toVersionString() {
        return String.valueOf(majorVersion) + '-' + minorVersion + '-' + snapshotVersion + '_' + prefix.getPrefix();
    }

    @AllArgsConstructor
    @Getter
    public enum Prefix {
        RELEASE('R'),
        STAGING('S'),
        UNSTABLE('U'),
        NIGHTLY('N');

        private final char prefix;

        public static Prefix parse(char c) {
            switch (c) {
                case 'R':
                    return RELEASE;
                case 'S':
                    return STAGING;
                case 'U':
                    return UNSTABLE;
                case 'N':
                    return NIGHTLY;
                default:
                    throw new IllegalArgumentException();
            }
        }

        public boolean isRelease() {
            return this == RELEASE;
        }

        public boolean isNightly() {
            return this == NIGHTLY;
        }
    }
}
