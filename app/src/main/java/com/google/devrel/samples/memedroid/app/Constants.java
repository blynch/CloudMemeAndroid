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

package com.google.devrel.samples.memedroid.app;

import android.content.Context;

import com.appspot.cloudmemebackend.cloudmeme.Cloudmeme;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

/**
 * Holding class for some static methods and configuration.
 */
public class Constants {
    // Configuration constants.
    // Client ID for the cloud endpoints backend.
    public static final String SERVER_CLIENTID = "354793178210.apps.googleusercontent.com";

    // General constants.
    public static final long REFRESH_MILLISECONDS = 60000;
    public static final long BLANK_REFRESH_MILLISECONDS = 1000;
    public static final String MEME_IMAGE_URL = "imageUrl";
    public static final String MEME_TEXT = "text";
    public static final String MEME_CREDENTIAL = "account";
    public static final String PREFS_NAME = "Memedroid";
    public static final String PREF_ACCOUNT_NAME = "accountName";
    public static final Boolean USE_AUTH = true;
    public static final int MAX_BACKOFF = 64;
    public static final String MEME_ID = "memeId";
    public static final String ACTION = "action";
    public static final String TEST_EMAIL_ADDRESS = "test@example.com";

    private static Cloudmeme mService;
    private static Boolean mForceInjectedService = false;

    // Prevent this class from being instantiated.
    private Constants() {
        throw new AssertionError();
    }

    /**
     * Retrieve a Memedroid service to use for making calls to the Cloud Endpoints backend.
     * @return Memedroid
     */
    public static Cloudmeme buildService(GoogleAccountCredential credential) {
        if (mService == null || (credential != null && !mForceInjectedService)) {
            mService = new Cloudmeme.Builder( AndroidHttp.newCompatibleTransport(),
                            new AndroidJsonFactory(), credential)
                            .setApplicationName("Memedroid Android")
                            .build();
        }
        return mService;
    }

    /**
     * Retrieve a GoogleAccountCredential used to authorise requests made to the cloud endpoints
     * backend.
     *
     * @param context application context
     * @param accountName the account selected
     * @return
     */
    public static GoogleAccountCredential getCredential(Context context, String accountName) {
        GoogleAccountCredential credential = GoogleAccountCredential.usingAudience(context,
                "server:client_id:" + Constants.SERVER_CLIENTID);
        // Small workaround to avoid setting an account that doesn't exist, so we can test.
        if (!TEST_EMAIL_ADDRESS.equals(accountName)) {
            credential.setSelectedAccountName(accountName);
        }
        return credential;
    }

    /**
     * Override the default constructed Memedroid service as returned by buildService.
     * @param service
     */
    public static void setMemeDroidService(Cloudmeme service) {
        mService = service;
        mForceInjectedService = true;
    }
}
