package com.alesharik.localstorage.version;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class VersionTest {
    private static final File fake = new File("");

    @Test
    public void testMajorCompare() throws Exception {
        Version lesser = new Version(1, 0, 0, Version.Prefix.RELEASE, fake);
        Version greater = new Version(2, 0, 0, Version.Prefix.RELEASE, fake);
        assertEquals(-1, lesser.compareTo(greater));
        assertEquals(1, greater.compareTo(lesser));
    }

    @Test
    public void testMinorCompare() throws Exception {
        Version lesser = new Version(2, 1, 0, Version.Prefix.RELEASE, fake);
        Version greater = new Version(2, 2, 0, Version.Prefix.RELEASE, fake);
        assertEquals(-1, lesser.compareTo(greater));
        assertEquals(1, greater.compareTo(lesser));
    }

    @Test
    public void testSnapshotCompare() throws Exception {
        Version lesser = new Version(2, 2, 1, Version.Prefix.RELEASE, fake);
        Version greater = new Version(2, 2, 2, Version.Prefix.RELEASE, fake);
        assertEquals(-1, lesser.compareTo(greater));
        assertEquals(1, greater.compareTo(lesser));
    }

    @Test
    public void parseTest() throws Exception {
        File file = mock(File.class);
        Version version = Version.fromVersionString("1-2-3_R", file);
        assertEquals(1, version.getMajorVersion());
        assertEquals(2, version.getMinorVersion());
        assertEquals(3, version.getSnapshotVersion());
        assertEquals(Version.Prefix.RELEASE, version.getPrefix());
        assertEquals(file, version.getFile());
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseIllegalStringTest() throws Exception {
        assertNull(Version.fromVersionString("1-2-3-R", mock(File.class)));
        fail();
    }

    @Test
    public void toVersionStringTest() throws Exception {
        Version version = new Version(1, 2, 3, Version.Prefix.STAGING, mock(File.class));
        assertEquals("1-2-3_S", version.toVersionString());
    }

    @Test
    public void testPrefixParse() throws Exception {
        for(Version.Prefix prefix : Version.Prefix.values()) {
            assertEquals(prefix, Version.Prefix.parse(prefix.getPrefix()));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPrefixParseUnexpectedChar() throws Exception {
        assertNull(Version.Prefix.parse('W'));
        fail();
    }
}