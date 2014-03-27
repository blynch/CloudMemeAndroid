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

import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.devrel.samples.memedroid.app.meme.MemeReceiver;
import com.google.devrel.samples.memedroid.app.meme.MemeService;

import android.accounts.AccountManager;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

/**
 * Main entry point and code activity for application. Hosts the fragment for the stream
 * and provides the links to the meme creation. Actions creation and auth process.
 */
public class MainActivity extends FragmentActivity
        implements MemeReceiver.MemeListener, VoteListener {

    private static final int REQ_CREATE_MEME = 53322;
    private static final int REQ_GOOGLE_PLAY_SERVICES = 53321;
    private static final int REQ_CHOOSE_ACCOUNT = 53320;
    private static final String TAG_STREAM = "StreamFrag";
    private static final String KEY_PENDING_VOTE = "pendingVote";

    private MemeReceiver mReceiver;
    private String mAccountName;
    private String mPendingVote = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new StreamFragment(), TAG_STREAM)
                    .commit();
        }
        if (savedInstanceState != null) {
            mPendingVote = savedInstanceState.getString(KEY_PENDING_VOTE);
        }

        getAccountName();
        invalidateOptionsMenu();
        mReceiver = new MemeReceiver(new Handler());
        mReceiver.setListener(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(KEY_PENDING_VOTE, mPendingVote);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.menu_logout);
        if (item != null) {
            item.setVisible(mAccountName != null);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_refresh) {
            maybeRefreshStream();
        } else if (id == R.id.menu_logout) {
            storeAccountName(null);
            invalidateOptionsMenu();
        } else if (id == R.id.menu_new) {
            if (Constants.USE_AUTH && mAccountName == null) {
                return triggerAccountSelection();
            } else {
                triggerCreation();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_CREATE_MEME && resultCode == RESULT_OK) {
            // MEME CREATION RESPONSE.
            Intent intent = new Intent(MemeService.ACTION_CREATE, null, this, MemeService.class);
            intent.putExtra(Constants.MEME_IMAGE_URL, data.getStringExtra(Constants.MEME_IMAGE_URL));
            intent.putExtra(Constants.MEME_TEXT, data.getStringExtra(Constants.MEME_TEXT));
            intent.putExtra(Constants.MEME_CREDENTIAL,getAccountName());
            intent.putExtra("receiver", mReceiver);
            startService(intent);
            this.setProgressBarIndeterminateVisibility(true);
        } else if (requestCode == REQ_CHOOSE_ACCOUNT && resultCode == RESULT_OK) {
            // ACCOUNT CHOOSER RESPONSE.
            String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
            storeAccountName(accountName);
            invalidateOptionsMenu();
            if (mPendingVote != null) {
                triggerVoteCreation(mPendingVote);
                mPendingVote = null;
            } else {
                triggerCreation();
            }
        }
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        this.setProgressBarIndeterminateVisibility(false);
        if (resultCode == RESULT_OK) {
            maybeRefreshStream();
            if (resultData != null &&
                    MemeService.ACTION_CREATE.equals(resultData.getString(Constants.ACTION)) ) {
                Toast.makeText(this, getString(R.string.meme_posted), Toast.LENGTH_SHORT).  show();
            } else {
                Toast.makeText(this, getString(R.string.meme_vote), Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    public void registerVote(String memeId) {
        if (mAccountName == null) {
            mPendingVote = memeId;
            triggerAccountSelection();
        } else {
            triggerVoteCreation(memeId);
        }
    }

    /**
     * Refresh the stream, if the stream is the currently displayed fragment.
     */
    private void maybeRefreshStream() {
        StreamFragment frag = (StreamFragment) getFragmentManager().findFragmentByTag(TAG_STREAM);
        if (frag != null) {
            // Update the stream to pick up the new meme.
            frag.refreshLoad();
        }
    }

    /**
     * Utility method that checks whether Google Play Services is installed and up to
     * date, and displays a notification for the user to update if no.
     *
     * @return true if installed
     */
    private boolean checkGooglePlayServicesAvailable() {
        final int connectionStatusCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(
                this);
        if (GooglePlayServicesUtil.isUserRecoverableError(connectionStatusCode)) {
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(
                    connectionStatusCode, this, REQ_GOOGLE_PLAY_SERVICES);
            dialog.show();
            return false;
        }
        return true;
    }

    /**
     * Method to request the user choose an account.
     */
    private boolean triggerAccountSelection() {
        if (!checkGooglePlayServicesAvailable()) {
            return false;
        }
        Intent intent = AccountPicker.newChooseAccountIntent(null, null,
                new String[]{"com.google"}, false, getString(R.string.auth_description),
                null, null, null);
        startActivityForResult(intent, REQ_CHOOSE_ACCOUNT);
        return true;
    }

    /**
     * Method to start the creation UI flow.
     */
    private void triggerCreation() {
        Intent create = new Intent(this, CreateActivity.class);
        startActivityForResult(create, REQ_CREATE_MEME);
    }

    /**
     * Vote on a meme.
     *
     * @param memeId
     */
    private void triggerVoteCreation(String memeId) {
        // MEME CREATION RESPONSE.
        Intent intent = new Intent(MemeService.ACTION_VOTE, null, this, MemeService.class);
        intent.putExtra(Constants.MEME_ID, memeId);
        intent.putExtra(Constants.MEME_CREDENTIAL,getAccountName());
        intent.putExtra("receiver", mReceiver);
        startService(intent);
        this.setProgressBarIndeterminateVisibility(true);
    }

    /**
     * Store the account name in a shared preference. Account name can be null to effectively
     * sign the user out.
     *
     * @param accountName
     */
    private void storeAccountName(String accountName) {
        mAccountName = accountName;
        SharedPreferences settings = this.getSharedPreferences(Constants.PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(Constants.PREF_ACCOUNT_NAME, mAccountName);
        editor.commit();
    }

    /**
     * Retrieve the account name from shared preferences if not set.
     *
     * @return String account name.
     */
    private String getAccountName() {
        if (mAccountName == null) {
            SharedPreferences settings = this.getSharedPreferences(Constants.PREFS_NAME, 0);
            mAccountName = settings.getString(Constants.PREF_ACCOUNT_NAME, null);
        }
        return mAccountName;
    }
}
