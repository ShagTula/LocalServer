package com.alesharik.localstorage.version;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.locks.StampedLock;

/**
 * This class store and manage all versions
 */
@SuppressWarnings("WeakerAccess")
@ThreadSafe
public final class VersionList {
    private final Set<Version> versions = new TreeSet<>();
    private final StampedLock stampedLock = new StampedLock();

    @Nonnull
    public Set<Version> getVersions() {
        long lock = stampedLock.tryOptimisticRead();
        Set<Version> versions = Collections.unmodifiableSet(this.versions);
        if(!stampedLock.validate(lock)) {
            try {
                lock = stampedLock.readLock();
                versions = Collections.unmodifiableSet(this.versions);
            } finally {
                stampedLock.unlockRead(lock);
            }
        }
        return versions;
    }

    @Nullable
    public Version getRelease() {
        long lock = stampedLock.tryOptimisticRead();
        Version release = getReleaseInternal();
        if(!stampedLock.validate(lock)) {
            try {
                lock = stampedLock.readLock();
                release = getReleaseInternal();
            } finally {
                stampedLock.unlockRead(lock);
            }
        }
        return release;
    }

    @Nonnull
    public Set<Version> getNightlyBuilds() {
        Set<Version> ret = new HashSet<>();
        long lock = stampedLock.tryOptimisticRead();
        for(Version version : versions) {
            if(version.getPrefix().isNightly())
                ret.add(version);
        }
        if(!stampedLock.validate(lock)) {
            try {
                lock = stampedLock.readLock();
                ret.clear();
                for(Version version : versions) {
                    if(version.getPrefix().isNightly())
                        ret.add(version);
                }
            } finally {
                stampedLock.unlockRead(lock);
            }
        }
        return ret;
    }

    private Version getReleaseInternal() {
        for(Version version : versions) {
            if(version.getPrefix().isRelease())
                return version;
        }
        return null;
    }

    public void addVersion(Version version) {
        long lock = stampedLock.writeLock();
        try {
            if(version.getPrefix().isRelease()) {
                Version lastRelease = getReleaseInternal();
                if(lastRelease != null && lastRelease.compareTo(version) < 0) {
                    lastRelease.delete();
                    versions.remove(lastRelease);
                    versions.add(version);
                } else if(lastRelease == null)
                    versions.add(version);
            } else
                versions.add(version);
            checkInternal();
        } finally {
            stampedLock.unlockWrite(lock);
        }
    }

    void check() {
        long lock = stampedLock.writeLock();
        try {
            checkInternal();
        } finally {
            stampedLock.unlockWrite(lock);
        }
    }

    void checkInternal() {
        Version release = getReleaseInternal();

        int nightlyI = 0;
        List<Version> toDel = new ArrayList<>();
        Version[] nightly = new Version[15];

        Iterator<Version> iterator = versions.iterator();
        while(iterator.hasNext()) {
            Version next = iterator.next();
            if((release != null && next.compareTo(release) < 0)
                    || (next.getPrefix().isRelease() && !next.equals(release))) {
                next.delete();
                iterator.remove();
                continue;
            }
            if(next.getPrefix().isNightly()) {
                if(nightlyI < 15) {
                    nightly[nightlyI] = next;
                    nightlyI++;
                } else {
                    nightly[0].delete();//0 is oldest version, because TreeSet sort values
                    toDel.add(nightly[0]);
                    nightly[0] = next;
                }
            }
        }
        versions.removeAll(toDel);
    }

    public void deleteVersion(Version version) {
        long lock = stampedLock.writeLock();
        boolean ok;
        try {
            ok = versions.remove(version);
        } finally {
            stampedLock.unlockWrite(lock);
        }

        if(ok)
            version.delete();
    }

    public boolean isEmpty() {
        long lock = stampedLock.tryOptimisticRead();
        boolean empty = versions.isEmpty();
        if(!stampedLock.validate(lock)) {
            lock = stampedLock.readLock();
            try {
                empty = versions.isEmpty();
            } finally {
                stampedLock.unlockRead(lock);
            }
        }
        return empty;
    }

    public int size() {
        long lock = stampedLock.tryOptimisticRead();
        int size = versions.size();
        if(!stampedLock.validate(lock)) {
            lock = stampedLock.readLock();
            try {
                size = versions.size();
            } finally {
                stampedLock.unlockRead(lock);
            }
        }
        return size;
    }

    public boolean contains(Version version) {
        long lock = stampedLock.tryOptimisticRead();
        boolean contains = versions.contains(version);
        if(!stampedLock.validate(lock)) {
            lock = stampedLock.readLock();
            try {
                contains = versions.contains(version);
            } finally {
                stampedLock.unlockRead(lock);
            }
        }
        return contains;
    }
}
