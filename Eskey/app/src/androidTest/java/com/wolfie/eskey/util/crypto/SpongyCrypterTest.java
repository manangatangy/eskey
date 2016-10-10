package com.wolfie.eskey.util.crypto;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;

import static com.wolfie.eskey.util.crypto.SpongyCrypter.MEDIUM_SECRET_KEY_FACTORY_ALGORITHM;
import static com.wolfie.eskey.util.crypto.SpongyCrypter.STRONG_SECRET_KEY_FACTORY_ALGORITHM;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Created by david on 10/10/16.
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

/**
 * Although the SpongyCrypter doesn't use any android specific classes, it cannot execute
 * as a unit test on the host, because the spongy castle security provider jar isn't signed
 * as required by the oracle jdk.  The android jdk however does not require this.
 */
@RunWith(AndroidJUnit4.class)
public class SpongyCrypterTest {

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        assertEquals("com.wolfie.eskey", appContext.getPackageName());
    }

    @Test
    public void testNewMasterGenerationSucceeds() {
        String salt = SpongyCrypter.generateSalt();
        String masterKey1 = SpongyCrypter.generateMasterKey();

        SpongyCrypter ch = new SpongyCrypter(salt, STRONG_SECRET_KEY_FACTORY_ALGORITHM);
        ch.setPassword("wolfgang");
        String encryptedMasterKey = ch.encrypt(masterKey1);

        // salt and encryptedMasterKey are stored in the database.
//        Log.d("Crypter", "'" + salt + "'");
//        Log.d("Crypter", "'" + encryptedMasterKey + "'");

        ch = new SpongyCrypter(salt, STRONG_SECRET_KEY_FACTORY_ALGORITHM);
        ch.setPassword("wolfgang");
        String masterKey2 = ch.decrypt(encryptedMasterKey);
        assertEquals(masterKey1, masterKey2);
    }

    @Test
    public void testNewMasterGenerationFails() {
        String salt = SpongyCrypter.generateSalt();
        String masterKey1 = SpongyCrypter.generateMasterKey();

        SpongyCrypter ch = new SpongyCrypter(salt, STRONG_SECRET_KEY_FACTORY_ALGORITHM);
        ch.setPassword("wolfgang");
        String encryptedMasterKey = ch.encrypt(masterKey1);

        ch = new SpongyCrypter(salt, STRONG_SECRET_KEY_FACTORY_ALGORITHM);
        ch.setPassword("wolfganG");
        String masterKey2 = ch.decrypt(encryptedMasterKey);
        assertNotEquals(masterKey1, masterKey2);
        assertEquals(null, masterKey2);
    }

    @Test
    public void testDataEncryptionSucceeds() {
        // salt and encryptedMasterKey are fetched from the database.
        String salt = "b9a0525950ae68d9";
        String encryptedMasterKey = "62b847babe45f24a80b390cbdcbd199d0c293f6696503fca7eb345f145873ecdb06ef6dbf0ab7a70252aed0eb05f126087de5a427fc9fc3948dc8242d975964332f4efe4dfff8eb5b53949287062c3e5";

        String expectedMasterKey = "1b29241291ddf5476bfe213528fd5b66173170967979d23687c42e3ce857d6eb";

        SpongyCrypter ch = new SpongyCrypter(salt, STRONG_SECRET_KEY_FACTORY_ALGORITHM);
        ch.setPassword("wolfgang");
        String masterKey = ch.decrypt(encryptedMasterKey);
        assertEquals(expectedMasterKey, masterKey);

        SpongyCrypter ch2 = new SpongyCrypter(salt, MEDIUM_SECRET_KEY_FACTORY_ALGORITHM);
        ch2.setPassword(masterKey);

        String original = "some plain text string";
        String cipher = ch2.encrypt(original);
        String plain = ch2.decrypt(cipher);
        assertEquals(original, plain);
//        String masterKey = ch.decrypt(encryptedMasterKey);
//        Log.d("Crypter", "'" + masterKey + "'");
//
//        assertNotEquals(null, masterKey);
    }

}
