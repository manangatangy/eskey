package com.wolfie.eskey.presenter;

import org.junit.Test;
import java.util.regex.Pattern;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;
import static org.mockito.Mockito.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import android.content.Context;
import android.content.SharedPreferences;

import com.wolfie.eskey.R;

@RunWith(MockitoJUnitRunner.class)
public class LoginPresenterTest {

    private static final String FAKE_STRING = "HELLO WORLD";

    @Mock
    Context mMockContext;

    @Mock
    LoginPresenter.LoginUi mMockUi;

    @Test
    public void readStringFromContext_LocalizedString() {
        // Given a mocked Context injected into the object under test...
//        when(mMockContext.getString(R.string.hello_word))
//                .thenReturn(FAKE_STRING);

        LoginPresenter loginPresenter = new LoginPresenter(mMockUi);

        // ...when the string is returned from the object under test...
//        String result = myObjectUnderTest.getHelloWorldString();

        // ...then the result should be the expected one.
//        assertThat(result, is(FAKE_STRING));
    }
}
