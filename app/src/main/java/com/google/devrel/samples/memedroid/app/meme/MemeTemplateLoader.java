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
import com.appspot.cloudmemebackend.cloudmeme.model.CloudMemeTemplate;
import com.appspot.cloudmemebackend.cloudmeme.model.CloudMemeTemplateCollection;
import com.google.devrel.samples.memedroid.app.Constants;

import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;

import java.io.IOException;
import java.util.List;

/**
 * Loader for the meme templates, as used in the Create Meme view.
 */
public class MemeTemplateLoader extends AsyncTaskLoader<List<CloudMemeTemplate>> {
    private List<CloudMemeTemplate> mData;
    private Cloudmeme mService;

    /**
     * Construct the loader, which will build a Memedroid service to execute the
     * API calls.
     *
     * @param context
     */
    public MemeTemplateLoader(Context context) {
        super(context);
        mService = Constants.buildService(null);
    }

    /**
     * Called as part of the loading flow, this method will return a list of meme templates
     * retrieved from the API.
     *
     * @return List
     */
    @Override
    public List<CloudMemeTemplate> loadInBackground() {
        List<CloudMemeTemplate> data = null;
        try {
            CloudMemeTemplateCollection memes = mService.memes().listTemplates().execute();
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
    public void deliverResult(List<CloudMemeTemplate> data) {
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
