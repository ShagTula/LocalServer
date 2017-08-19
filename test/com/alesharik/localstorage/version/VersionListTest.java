package com.alesharik.localstorage.version;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.io.PrintStream;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("ResultOfMethodCallIgnored")
@RunWith(MockitoJUnitRunner.class)
public class VersionListTest {
    private static final Random RAND = new SecureRandom();

    static {
        RAND.setSeed(123);
    }

    private VersionList list;
    private File file;

    @Before
    public void setUp() throws Exception {
        list = new VersionList();
        PrintStream serrMock = mock(PrintStream.class);
        System.setErr(serrMock);

        file = File.createTempFile("AlesharikWebServer", "sfd");
    }

    @After
    public void tearDown() throws Exception {
        file.delete();
    }

    @Test
    public void testVersionEqualsLogic() throws Exception {
        File spy = spy(file);

        Version release1 = new Version(1, 2, 3, Version.Prefix.RELEASE, spy);
        Version release2 = new Version(1, 2, 3, Version.Prefix.RELEASE, spy);
        list.addVersion(release1);
        list.addVersion(release2);

        verify(spy, never()).delete();

        assertTrue(list.contains(release1));
    }

    @Test
    public void testMajorLogic() throws Exception {
        File spy = spy(file);

        Version release1 = new Version(1, 0, 0, Version.Prefix.RELEASE, spy);
        Version release2 = new Version(2, 0, 0, Version.Prefix.RELEASE, spy);
        list.addVersion(release1);
        list.addVersion(release2);

        verify(spy, times(1)).delete();
        assertFalse(list.contains(release1));
        assertTrue(list.contains(release2));
    }

    @Test
    public void testMajorLogicWithVersionNotApplied() throws Exception {
        File spy = spy(file);

        Version release1 = new Version(2, 0, 0, Version.Prefix.RELEASE, spy);
        Version release2 = new Version(1, 0, 0, Version.Prefix.RELEASE, spy);
        list.addVersion(release1);
        list.addVersion(release2);

        verify(spy, never()).delete();

        assertTrue(list.contains(release1));
        assertFalse(list.contains(release2));
    }

    @Test
    public void testMinorLogic() throws Exception {
        File spy = spy(file);

        Version release1 = new Version(1, 1, 0, Version.Prefix.RELEASE, spy);
        Version release2 = new Version(1, 2, 0, Version.Prefix.RELEASE, spy);
        list.addVersion(release1);
        list.addVersion(release2);

        verify(spy, times(1)).delete();
        assertFalse(list.contains(release1));
        assertTrue(list.contains(release2));
    }

    @Test
    public void testMinorLogicWithVersionNotApplied() throws Exception {
        File spy = spy(file);

        Version release1 = new Version(1, 2, 0, Version.Prefix.RELEASE, spy);
        Version release2 = new Version(1, 1, 0, Version.Prefix.RELEASE, spy);
        list.addVersion(release1);
        list.addVersion(release2);

        verify(spy, never()).delete();

        assertTrue(list.contains(release1));
        assertFalse(list.contains(release2));
    }

    @Test
    public void testSnapshotLogic() throws Exception {
        File spy = spy(file);

        Version release1 = new Version(1, 1, 1, Version.Prefix.RELEASE, spy);
        Version release2 = new Version(1, 1, 2, Version.Prefix.RELEASE, spy);
        list.addVersion(release1);
        list.addVersion(release2);

        verify(spy, times(1)).delete();
        assertFalse(list.contains(release1));
        assertTrue(list.contains(release2));
    }

    @Test
    public void testSnapshotLogicWithVersionNotApplied() throws Exception {
        File spy = spy(file);

        Version release1 = new Version(1, 1, 2, Version.Prefix.RELEASE, spy);
        Version release2 = new Version(1, 1, 1, Version.Prefix.RELEASE, spy);
        list.addVersion(release1);
        list.addVersion(release2);

        verify(spy, never()).delete();

        assertTrue(list.contains(release1));
        assertFalse(list.contains(release2));
    }

    @SuppressWarnings("ComparatorMethodParameterNotUsed")
    @Test
    public void testNightlyBuildLogic() throws Exception {
        File spy = spy(file);
        Version[] versions = new Version[16];
        for(int i = 0; i < versions.length; i++) {
            versions[i] = new Version(i, 0, 0, Version.Prefix.NIGHTLY, spy);
        }
        Version first = versions[0];

        Arrays.sort(versions, (o1, o2) -> RAND.nextInt());

        for(Version version : versions)
            list.addVersion(version);

        assertFalse(list.contains(first));
        verify(spy, times(1)).delete();

        for(Version version : versions) {
            if(version != first)
                assertTrue(list.contains(version));
        }

        Set<Version> builds = list.getNightlyBuilds();
        for(Version version : versions) {
            if(version != first)
                assertTrue(builds.contains(version));
        }
        assertEquals(15, builds.size());
    }

    @Test
    public void testOutdatedVersionsLogic() throws Exception {
        File spy = spy(file);

        Version release1 = new Version(1, 0, 0, Version.Prefix.RELEASE, spy);

        list.addVersion(release1);

        for(int i = 0; i < 10; i++) {
            list.addVersion(new Version(1, 1, i, Version.Prefix.STAGING, spy));
        }

        Version release2 = new Version(2, 0, 0, Version.Prefix.RELEASE, spy);
        list.addVersion(release2);

        verify(spy, times(11)).delete();
        assertEquals(1, list.size());
        assertTrue(list.contains(release2));
    }

    @Test
    public void testAddRemove() throws Exception {
        File spy = spy(file);
        Version v = new Version(2, 0, 0, Version.Prefix.RELEASE, spy);
        assertTrue(list.isEmpty());

        list.addVersion(v);
        assertEquals(1, list.size());

        list.deleteVersion(v);
        assertTrue(list.isEmpty());
        verify(spy, times(1)).delete();
    }

    @Test
    public void testGetRelease() throws Exception {
        Version version = new Version(2, 0, 0, Version.Prefix.RELEASE, file);
        assertNull(list.getRelease());
        list.addVersion(version);
        assertEquals(version, list.getRelease());
    }

    @Test
    public void testInsertDifferentPrefixes() throws Exception {
        Version version1 = new Version(2, 0, 0, Version.Prefix.RELEASE, file);
        Version version2 = new Version(2, 1, 0, Version.Prefix.STAGING, file);
        Version version3 = new Version(2, 1, 1, Version.Prefix.UNSTABLE, file);
        Version version4 = new Version(2, 1, 2, Version.Prefix.NIGHTLY, file);

        list.addVersion(version1);
        list.addVersion(version2);
        list.addVersion(version3);
        list.addVersion(version4);

        assertTrue(list.contains(version1));
        assertTrue(list.contains(version2));
        assertTrue(list.contains(version3));
        assertTrue(list.contains(version4));
    }

    @SuppressWarnings("ComparatorMethodParameterNotUsed")
    @Test
    public void testGetVersions() throws Exception {
        File spy = spy(file);

        Version[] versions = new Version[64];
        for(int i = 0; i < versions.length; i += 4) {
            versions[i] = new Version(i, i, i, Version.Prefix.RELEASE, spy);
            versions[i + 1] = new Version(i, i + 1, i, Version.Prefix.STAGING, spy);
            versions[i + 2] = new Version(i, i + 1, i + 1, Version.Prefix.UNSTABLE, spy);
            versions[i + 3] = new Version(i, i + 1, i + 2, Version.Prefix.NIGHTLY, spy);
        }

        Arrays.sort(versions, (o1, o2) -> RAND.nextInt());

        for(Version version : versions)
            list.addVersion(version);

        list.check();

        Set<Version> vers = list.getVersions();
        int releaseCount = 0;
        int nightCount = 0;
        for(Version ver : vers) {
            if(ver.getPrefix().isRelease())
                releaseCount++;
            if(ver.getPrefix().isNightly())
                nightCount++;
        }
        assertEquals(1, releaseCount);
        assertEquals(1, nightCount);
    }
}