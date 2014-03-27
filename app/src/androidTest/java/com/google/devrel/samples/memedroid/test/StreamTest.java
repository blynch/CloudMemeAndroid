/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.devrel.samples.memedroid.test;

import com.google.android.apps.common.testing.ui.espresso.action.ViewActions;
import com.google.devrel.samples.memedroid.app.Constants;
import com.google.devrel.samples.memedroid.app.MainActivity;
import com.google.devrel.samples.memedroid.app.R;

import android.content.SharedPreferences;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.LargeTest;

import com.appspot.cloudmemebackend.cloudmeme.Cloudmeme;
import com.appspot.cloudmemebackend.cloudmeme.model.CloudMemeMeme;
import com.appspot.cloudmemebackend.cloudmeme.model.CloudMemeMemeCollection;
import com.appspot.cloudmemebackend.cloudmeme.model.CloudMemeTemplate;
import com.appspot.cloudmemebackend.cloudmeme.model.CloudMemeTemplateCollection;

import java.io.IOException;
import java.util.ArrayList;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static com.google.android.apps.common.testing.ui.espresso.Espresso.pressBack;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.doesNotExist;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isDisplayed;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Basic test class to test the features of the general stream.
 */
@LargeTest
public class StreamTest extends ActivityInstrumentationTestCase2<MainActivity> {

    @SuppressWarnings("deprecation")
    public StreamTest() {
        // This constructor was deprecated - but we want to support lower API levels.
        super("com.google.android.apps.common.testing.ui.testapp", MainActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    /**
     * Test that we provide a message for when there is no data available.
     */
    public void testNoMemesMessageIsDisplayed() throws IOException {
        Cloudmeme.Memes memes = getBlankedMemedroidMemes();

        getActivity();
        onView(withId(android.R.id.empty))
                .check(matches(withText(R.string.blank_list)))
                .check(matches(isDisplayed()));
    }

    /**
     * Test that a stream appears and has posts in it.
     */
    public void testStreamHasPosts() throws IOException {
        Cloudmeme mockedDroid = mock(Cloudmeme.class);
        Cloudmeme.Memes mockedMemes = mock(Cloudmeme.Memes.class);
        Cloudmeme.Memes.List mockedList = mock(Cloudmeme.Memes.List.class);
        final ArrayList<CloudMemeMeme> memes = new ArrayList<CloudMemeMeme>();
        CloudMemeMeme meme = new CloudMemeMeme();
        meme.setImageUrl("http://example.com/image.jpg");
        memes.add(0, meme);
        CloudMemeMemeCollection mc = new CloudMemeMemeCollection();
        mc.setItems(memes);
        when(mockedList.execute()).thenReturn(mc);
        when(mockedMemes.list()).thenReturn(mockedList);
        when(mockedDroid.memes()).thenReturn(mockedMemes);
        Constants.setMemeDroidService(mockedDroid);

        getActivity();
        onView(withId(android.R.id.empty))
                .check(matches(withText(R.string.blank_list)))
                .check(matches(not(isDisplayed())));
        onView(withId(R.id.meme_list))
                .check(matches(isDisplayed()));
        // Test we have a meme image.
        onView(withId(R.id.meme_image))
                .check(matches(isDisplayed()));
    }

    /**
     * Test pressing the refresh button properly.
     */
    public void testRefreshTriggersStreamRefresh() throws IOException {
        Cloudmeme mockedDroid = mock(Cloudmeme.class);
        Cloudmeme.Memes mockedMemes = mock(Cloudmeme.Memes.class);
        Cloudmeme.Memes.List mockedList = mock(Cloudmeme.Memes.List.class);
        final ArrayList<CloudMemeMeme> memes = new ArrayList<CloudMemeMeme>();
        CloudMemeMemeCollection mc = new CloudMemeMemeCollection();
        mc.setItems(memes);
        when(mockedList.execute()).thenReturn(mc);
        when(mockedMemes.list()).thenReturn(mockedList);
        when(mockedDroid.memes()).thenReturn(mockedMemes);
        Constants.setMemeDroidService(mockedDroid);

        getActivity();
        onView(withId(android.R.id.empty))
                .check(matches(withText(R.string.blank_list)))
                .check(matches(isDisplayed()));
        CloudMemeMeme meme = new CloudMemeMeme();
        meme.setImageUrl("http://example.com/image.jpg");
        memes.add(0, meme);
        onView(withId(R.id.menu_refresh)).perform(ViewActions.click());
        onView(withId(android.R.id.empty))
                .check(matches(withText(R.string.blank_list)))
                .check(matches(not(isDisplayed())));

    }

    /**
     * Test that if we are signed out we can sign in again,
     * and if we are signed in we can sign out.
     */
    public void testSignInSignOut() throws IOException {
        Cloudmeme.Memes memes = getBlankedMemedroidMemes();

        // Set the state of the activity to signed in.
        SharedPreferences settings = getInstrumentation ().getTargetContext().getSharedPreferences(Constants.PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(Constants.PREF_ACCOUNT_NAME, Constants.TEST_EMAIL_ADDRESS);
        editor.commit();

        getActivity();

        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());

        onView(withText(R.string.sign_out))
            .check(matches(isDisplayed()));

        // Logout
        onView(withText(R.string.sign_out))
            .perform(ViewActions.click());

        // Test that menu option is gone when signed out.
        onView(withText(R.string.sign_out))
                .check(doesNotExist());
    }

    /**
     * Test that the create button takes us to the create activity.
     */
    public void testCreateStartsActivity() throws IOException {
        Cloudmeme.Memes mockedMemes = getBlankedMemedroidMemes();
        Cloudmeme.Memes.ListTemplates mockedList = mock(Cloudmeme.Memes.ListTemplates.class);
        CloudMemeTemplateCollection mc = new CloudMemeTemplateCollection();
        final ArrayList<CloudMemeTemplate> data = new ArrayList<CloudMemeTemplate>();
        mc.setItems(data);
        when(mockedList.execute()).thenReturn(mc);
        when(mockedMemes.listTemplates()).thenReturn(mockedList);

        // Set the state of the activity to signed in.
        SharedPreferences settings =
            getInstrumentation ().getTargetContext().getSharedPreferences(Constants.PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(Constants.PREF_ACCOUNT_NAME, Constants.TEST_EMAIL_ADDRESS);
        editor.commit();

        getActivity();

        onView(withId(R.id.menu_new))
                .perform(ViewActions.click());

        onView(withId(R.id.top_text))
                .check(matches(isDisplayed()));

        pressBack(); // keyboard
        pressBack(); // back

        onView(withId(R.id.menu_new))
                .check(matches(isDisplayed()));
    }

    /**
     * Test finding and pressing the vote button.
     *
     * @throws IOException
     */
    public void testVote() throws IOException {
        Cloudmeme mockedDroid = mock(Cloudmeme.class);
        Cloudmeme.Memes mockedMemes = mock(Cloudmeme.Memes.class);
        Cloudmeme.Memes.List mockedList = mock(Cloudmeme.Memes.List.class);
        final ArrayList<CloudMemeMeme> memes = new ArrayList<CloudMemeMeme>();
        CloudMemeMeme meme = new CloudMemeMeme();
        meme.setImageUrl("http://example.com/image.jpg");
        meme.setText("Hello");
        meme.setId("123");
        memes.add(0, meme);
        CloudMemeMemeCollection mc = new CloudMemeMemeCollection();
        mc.setItems(memes);
        when(mockedList.execute()).thenReturn(mc);
        when(mockedMemes.list()).thenReturn(mockedList);
        when(mockedDroid.memes()).thenReturn(mockedMemes);
        Constants.setMemeDroidService(mockedDroid);

        getActivity();
        onView(withId(R.id.meme_vote_button))
                .check(matches(isDisplayed()))
                .perform(ViewActions.click());
    }

    /**
     * Helper method to return a mocked MemedroidMeme object.
     *
     * @return Cloudmeme.Memes mock
     * @throws IOException
     */
    private Cloudmeme.Memes getBlankedMemedroidMemes() throws IOException {
        Cloudmeme mockedDroid = mock(Cloudmeme.class);
        Cloudmeme.Memes mockedMemes = mock(Cloudmeme.Memes.class);
        Cloudmeme.Memes.ListTemplates mockedTemplateList = mock(Cloudmeme.Memes.ListTemplates.class);
        CloudMemeTemplateCollection mtc = new CloudMemeTemplateCollection();
        final ArrayList<CloudMemeTemplate> data = new ArrayList<CloudMemeTemplate>();
        mtc.setItems(data);
        when(mockedTemplateList.execute()).thenReturn(mtc);
        when(mockedMemes.listTemplates()).thenReturn(mockedTemplateList);
        Cloudmeme.Memes.List mockedList = mock(Cloudmeme.Memes.List.class);
        final ArrayList<CloudMemeMeme> memes = new ArrayList<CloudMemeMeme>();
        CloudMemeMemeCollection mc = new CloudMemeMemeCollection();
        mc.setItems(memes);
        when(mockedList.execute()).thenReturn(mc);
        when(mockedMemes.list()).thenReturn(mockedList);
        when(mockedDroid.memes()).thenReturn(mockedMemes);
        Constants.setMemeDroidService(mockedDroid);
        return mockedMemes;
    }
}
