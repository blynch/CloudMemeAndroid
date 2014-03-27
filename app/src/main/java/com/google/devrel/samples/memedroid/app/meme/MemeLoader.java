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
import com.appspot.cloudmemebackend.cloudmeme.model.CloudMemeMemeCollection;
import com.google.devrel.samples.memedroid.app.Constants;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.io.IOException;
import java.util.List;

/**
 * Loader for the main meme feed.
 */
public class MemeLoader extends AsyncTaskLoader<List<CloudMemeMeme>> {
    private List<CloudMemeMeme> mData;
    private Cloudmeme mService;

    /**
     * Construct the loader, which will build a Memedroid service to execute the
     * API calls.
     *
     * @param context
     */
    public MemeLoader(Context context) {
        super(context);
        mService = Constants.buildService(null);
    }

    /**
     * Called as part of the loading flow, this method will return a list of memes
     * retrieved from the API.
     *
     * @return List
     */
    @Override
    public List<CloudMemeMeme> loadInBackground() {
        List<CloudMemeMeme> data = null;
        try {
            CloudMemeMemeCollection memes = mService.memes().list().execute();
            data = memes.getItems();
        } catch (IOException e) {
            data = null;
        }

        return data;
    }

    /**
     * Deliver the data to the caller, if currently started.
     *
     * @param data
     */
    @Override
    public void deliverResult(List<CloudMemeMeme> data) {
        mData = data;

        if(isStarted()) {
            super.deliverResult(mData);
        }
    }

    /**
     * Called when first loading, checks whether a load needs to occur or the
     * previously retrieved data can be used.
     */
    @Override
    protected void onStartLoading() {
        if(mData != null) {
            deliverResult(mData);
        }

        if(takeContentChanged() || mData == null) {
            forceLoad();
        }
    }
}
