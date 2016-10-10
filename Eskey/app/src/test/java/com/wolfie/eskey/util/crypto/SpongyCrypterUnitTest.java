package com.wolfie.eskey.util.crypto;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by david on 10/10/16.
 */

public class SpongyCrypterUnitTest {

    @Test
    public void byteConversion() {
        assertEquals("0aff00", SpongyCrypter.toHexString(new byte[] {10, (byte)0xff, 0}));
        assertEquals("0aff00", SpongyCrypter.toHexString(new byte[] {10, (byte)-1, 0}));
    }

    @Test
    public void hexConversion()  {
        checkEquals(new byte[] {10, (byte)0xff, 0}, SpongyCrypter.fromHexString("0aFF00"));
        try {
            SpongyCrypter.fromHexString("0aFF001");
            fail("expected exception did not occur");
        } catch (StringIndexOutOfBoundsException exc) {
        }
        try {
            SpongyCrypter.fromHexString("0aFF00zz");
            fail("expected exception did not occur");
        } catch (NumberFormatException exc) {
        }
    }

    public void checkEquals(byte[] expected, byte[] actual) {
        assertEquals(expected.length, actual.length);
        for (int i = 0; i < actual.length; i++) {
            assertEquals(expected[i], actual[i]);
        }
    }

}
