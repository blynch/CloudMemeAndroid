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

import com.google.devrel.samples.memedroid.app.Constants;
import com.google.devrel.samples.memedroid.app.CreateActivity;
import com.google.devrel.samples.memedroid.app.R;

import android.app.Instrumentation;
import android.test.ActivityInstrumentationTestCase2;

import com.appspot.cloudmemebackend.cloudmeme.Cloudmeme;
import com.appspot.cloudmemebackend.cloudmeme.model.CloudMemeTemplate;
import com.appspot.cloudmemebackend.cloudmeme.model.CloudMemeTemplateCollection;

import java.io.IOException;
import java.util.ArrayList;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.click;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.typeText;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isDisplayed;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isEnabled;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test the create meme activity.
 */
public class CreateMemeTest extends ActivityInstrumentationTestCase2<CreateActivity> {

    @SuppressWarnings("deprecation")
    public CreateMemeTest() {
        // This constructor was deprecated - but we want to support lower API levels.
        super("com.google.android.apps.common.testing.ui.testapp", CreateActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    /**
     * Test that we have a loading memes indicator text.
     */
    public void testLoadingMemesMessage() throws IOException {
        Cloudmeme mockedDroid = mock(Cloudmeme.class);
        Cloudmeme.Memes mockedMemes = mock(Cloudmeme.Memes.class);
        Cloudmeme.Memes.ListTemplates mockedList = mock(Cloudmeme.Memes.ListTemplates.class);
        CloudMemeTemplateCollection mc = new CloudMemeTemplateCollection();
        final ArrayList<CloudMemeTemplate> data = new ArrayList<CloudMemeTemplate>();
        mc.setItems(data);
        when(mockedList.execute()).thenReturn(mc);
        when(mockedMemes.listTemplates()).thenReturn(mockedList);
        when(mockedDroid.memes()).thenReturn(mockedMemes);
        Constants.setMemeDroidService(mockedDroid);

        getActivity();
            onView(withId(R.id.meme_template_empty))
            .check(matches(withText(R.string.meme_template_empty)));
    }

    /**
     * Test that we have the expected form fields.
     */
    public void testProperFormFieldsPresent() throws IOException {
        Cloudmeme mockedDroid = mock(Cloudmeme.class);
        Cloudmeme.Memes mockedMemes = mock(Cloudmeme.Memes.class);
        Cloudmeme.Memes.ListTemplates mockedList = mock(Cloudmeme.Memes.ListTemplates.class);
        CloudMemeTemplateCollection mc = new CloudMemeTemplateCollection();
        final ArrayList<CloudMemeTemplate> data = new ArrayList<CloudMemeTemplate>();
        mc.setItems(data);
        when(mockedList.execute()).thenReturn(mc);
        when(mockedMemes.listTemplates()).thenReturn(mockedList);
        when(mockedDroid.memes()).thenReturn(mockedMemes);
        Constants.setMemeDroidService(mockedDroid);

        String memeText = "hello world";

        Instrumentation.ActivityMonitor monitor = getInstrumentation().addMonitor(
                CreateActivity.class.getName(), null, false);

        getActivity();
        onView(withId(R.id.top_text))
                .check(matches(isDisplayed()));

        onView(withId(R.id.top_text))
                .perform(click())
                .perform(typeText(memeText))
                .perform(click());

        onView(withId(R.id.top_text))
                .check(matches(withText(memeText)));
    }

    /**
     * Test that we can't create a meme until we have
     * entered some text.
     */
    public void testCreateNotEnabledUntilTextEntered() throws IOException {
        Cloudmeme mockedDroid = mock(Cloudmeme.class);
        Cloudmeme.Memes mockedMemes = mock(Cloudmeme.Memes.class);
        Cloudmeme.Memes.ListTemplates mockedList = mock(Cloudmeme.Memes.ListTemplates.class);
        CloudMemeTemplateCollection mc = new CloudMemeTemplateCollection();
        final ArrayList<CloudMemeTemplate> data = new ArrayList<CloudMemeTemplate>();
        mc.setItems(data);
        when(mockedList.execute()).thenReturn(mc);
        when(mockedMemes.listTemplates()).thenReturn(mockedList);
        when(mockedDroid.memes()).thenReturn(mockedMemes);
        Constants.setMemeDroidService(mockedDroid);

        getActivity();
        onView(withId(R.id.top_text))
                .check(matches(withText("")));
        onView(withId(R.id.menu_create))
                .check(matches(not(isEnabled())));
        onView(withId(R.id.top_text))
                .perform(typeText("Hello World"));
        onView(withId(R.id.menu_create))
                .check(matches(isEnabled()));

    }

}
