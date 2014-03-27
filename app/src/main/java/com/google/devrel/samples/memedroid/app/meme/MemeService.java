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

package com.google.devrel.samples.memedroid.app.meme;

import com.appspot.cloudmemebackend.cloudmeme.Cloudmeme;
import com.appspot.cloudmemebackend.cloudmeme.model.CloudMemeMeme;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.devrel.samples.memedroid.app.Constants;

import android.app.Activity;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;

import java.io.IOException;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests to
 * the Memedroid backend via the Cloud Endpoints generated SDK.
 */
public class MemeService extends IntentService {
    public static final int RESULT_ERR = -1;
    public static final String ACTION_CREATE =
            "com.google.devrel.samples.memedroid.app.meme.action.CREATE";
    public static final String ACTION_VOTE =
            "com.google.devrel.samples.memedroid.app.meme.action.VOTE";
    private Cloudmeme mService;

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionList(Context context) {
        Intent intent = new Intent(context, MemeService.class);
        intent.setAction(ACTION_CREATE);
        context.startService(intent);
    }

    /**
     * Create an IntentService for wrapping calls to the Memedroid API.
     */
    public MemeService() {
        super("MemeService");
    }

    /**
     * Process the Intents, make appropriate API calls.
     *
     * @param intent
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final ResultReceiver receiver = intent.getParcelableExtra("receiver");
            final String action = intent.getAction();
            final String account = intent.getStringExtra(Constants.MEME_CREDENTIAL);
            GoogleAccountCredential credential = null;
            if (account != null) {
                credential = Constants.getCredential(getApplicationContext(), account);
            }
            mService = Constants.buildService(credential);
            if (ACTION_CREATE.equals(action)) {
                final String imageUrl = intent.getStringExtra(Constants.MEME_IMAGE_URL);
                final String text = intent.getStringExtra(Constants.MEME_TEXT);
                CloudMemeMeme meme = new CloudMemeMeme();
                meme.setImageUrl(imageUrl);
                meme.setText(text);
                handleActionCreate(receiver, meme);
            } else if (ACTION_VOTE.equals(action)) {
                final String memeId = intent.getStringExtra(Constants.MEME_ID);
                CloudMemeMeme meme = new CloudMemeMeme();
                meme.setId(memeId);
                handleActionVote(receiver, meme);
            }
        }
    }

    /**
     * Create a meme in the background.
     */
    private void handleActionCreate(ResultReceiver receiver, CloudMemeMeme meme) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.ACTION, ACTION_CREATE);
        int result = Activity.RESULT_OK;
        try {
            CloudMemeMeme response = mService.memes().create(meme).execute();
            if (!response.getText().equals(meme.getText())) {
                result = RESULT_ERR;
            }
        } catch (IOException e) {
            result = RESULT_ERR;
        }
        receiver.send(result, bundle);
    }

    /**
     * Vote for a meme in the background.
     */
    private void handleActionVote(ResultReceiver receiver, CloudMemeMeme meme) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.ACTION, ACTION_VOTE);
        // TODOL Impement when the backend is ready.
        int result = Activity.RESULT_OK;
        receiver.send(result, bundle);
    }
}
