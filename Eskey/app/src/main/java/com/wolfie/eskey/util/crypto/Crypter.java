package com.wolfie.eskey.util.crypto;

import android.support.annotation.Nullable;

/**
 * Created by david on 4/09/16.
 */

public interface Crypter {

    String encrypt(@Nullable String plainText);

    String decrypt(@Nullable String cipherText);

}
