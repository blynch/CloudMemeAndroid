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

import com.google.devrel.samples.memedroid.app.meme.VolleyContainer;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.android.volley.toolbox.NetworkImageView;
import com.appspot.cloudmemebackend.cloudmeme.model.CloudMemeTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Grid View adapter for displaying meme templates.
 */
public class MemeTemplateAdapter extends ArrayAdapter<CloudMemeTemplate>
        implements TemplateAdapter {
    private static final String TAG = "MemeDroid-MemeTemplateAdapter";
    private int mResourceId;
    private Context mContext;
    private VolleyContainer mVolley;
    private int mSelected = 0;

    public MemeTemplateAdapter(Context context, int resource) {
        super(context, resource, new ArrayList<CloudMemeTemplate>());
        mResourceId = resource;
        mContext = context;
        mVolley = VolleyContainer.getInstance(context);
    }

    @Override
    public void setData(List<CloudMemeTemplate> data) {
        clear();
        if (data != null) {
            addAll(data);
            notifyDataSetChanged();
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate(mResourceId, parent, false);
        }
        CloudMemeTemplate meme = getItem(position);
        if (meme != null && convertView != null) {
            markView(convertView, mSelected == position);
            NetworkImageView image =
                    (NetworkImageView) convertView.findViewById(R.id.meme_template_image);
            String imageUrl = meme.getUrl();
            Log.d(TAG, "Requesting template: " + imageUrl);
            image.setImageUrl(imageUrl, mVolley.getImageLoader());
            image.setDefaultImageResId(R.drawable.ic_action_camera_wide);
            image.setErrorImageResId(R.drawable.ic_action_camera_wide);
        }
        return convertView;
    }

    @Override
    public void setSelectedItem(int item) {
        if (mSelected == item) {
            return;
        }
        mSelected = item;
        notifyDataSetChanged();
    }

    /**
     * Set the background based on whether or not the item is the current selection.
     *
     * @param v view to mark
     * @param selected true to indicate selected
     */
    private void markView(View v, boolean selected) {
        if (v != null) {
            int colourId = selected ? R.color.purple : R.color.white;
            v.setBackgroundColor(mContext.getResources().getColor(colourId));
        }
    }
}