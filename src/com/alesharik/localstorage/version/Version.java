package com.alesharik.localstorage.version;

import com.alesharik.webserver.logger.Prefixes;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import sun.misc.Cleaner;

import javax.annotation.MatchesPattern;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.util.regex.Pattern;

/**
 * This class represents client application version
 */
@SuppressWarnings("WeakerAccess")
@Immutable
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
    private final RandomAccessFile randomAccessFile;

    public Version(int majorVersion, int minorVersion, int snapshotVersion, Prefix prefix, File file) {
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
        this.snapshotVersion = snapshotVersion;
        this.prefix = prefix;
        this.file = file;
        try {
            this.randomAccessFile = new RandomAccessFile(file, "r");
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
        Cleaner.create(this, () -> {
            try {
                randomAccessFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public Version(int majorVersion, int minorVersion, int snapshotVersion, Prefix prefix) {
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
        this.snapshotVersion = snapshotVersion;
        this.prefix = prefix;
        this.file = null;
        this.randomAccessFile = null;
    }

    public static Version fromVersionString(@MatchesPattern("\\d-\\d-\\d_[RSUN]") @Nonnull String name) {
        if(!VERSION_PATTERN.matcher(name).matches())
            throw new IllegalArgumentException();

        String[] parts = name.split("[-_]");
        return new Version(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Prefix.parse(parts[3].charAt(0)));
    }

    public static Version fromVersionString(@MatchesPattern("\\d-\\d-\\d_[RSUN]") @Nonnull String name, @Nonnull File file) {
        if(!VERSION_PATTERN.matcher(name).matches())
            throw new IllegalArgumentException();

        String[] parts = name.split("[-_]");
        return new Version(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Prefix.parse(parts[3].charAt(0)), file);
    }

    /**
     * Return read-only {@link RandomAccessFile}
     */
    public RandomAccessFile getRandomAccessFile() {
        return randomAccessFile;
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
        if(file == null || randomAccessFile == null)
            return;
        try {
            randomAccessFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(!file.delete())
            System.err.println("Can't delete file " + file.toString() + " !");
    }

    public String toVersionString() {
        return String.valueOf(majorVersion) + '-' + minorVersion + '-' + snapshotVersion + '_' + prefix.getPrefix();
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof Version)) return false;

        Version version = (Version) o;

        if(getMajorVersion() != version.getMajorVersion()) return false;
        if(getMinorVersion() != version.getMinorVersion()) return false;
        if(getSnapshotVersion() != version.getSnapshotVersion()) return false;
        return getPrefix() == version.getPrefix();
    }

    @Override
    public int hashCode() {
        int result = getMajorVersion();
        result = 31 * result + getMinorVersion();
        result = 31 * result + getSnapshotVersion();
        result = 31 * result + (getPrefix() != null ? getPrefix().hashCode() : 0);
        return result;
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
