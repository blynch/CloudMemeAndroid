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

import com.appspot.cloudmemebackend.cloudmeme.model.CloudMemeMeme;
import com.google.devrel.samples.memedroid.app.meme.VolleyContainer;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

/**
 * A simple array adapter that holds memes.
 */
class MemeAdapter extends ArrayAdapter<CloudMemeMeme> implements View.OnClickListener {
    private static final String TAG = "MemeDroid-MemeAdapter";
    private int mResourceId;
    private Context mContext;
    private VolleyContainer mVolley;
    private VoteListener mVoteListener;

    public MemeAdapter(Context context, int resource, List<CloudMemeMeme> objects) {
        super(context, resource, objects);
        mResourceId = resource;
        mContext = context;
        mVolley = VolleyContainer.getInstance(context);
    }

    /**
     * Set the listener that handles vote actions.
     *
     * @param voteListener
     */
    public void setVoteListener(VoteListener voteListener) {
        mVoteListener = voteListener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate(mResourceId, parent, false);
        }
        CloudMemeMeme meme = getItem(position);
        if (meme != null && convertView != null) {
            NetworkImageView image = (NetworkImageView) convertView.findViewById(R.id.meme_image);
            String imageUrl = meme.getImageUrl();
            Log.d(TAG, "Requesting Image: " + imageUrl);
            image.setContentDescription(meme.getText());
            image.setImageUrl(imageUrl, mVolley.getImageLoader());
            image.setDefaultImageResId(R.drawable.ic_action_camera_wide);
            image.setErrorImageResId(R.drawable.ic_action_camera_wide);
            TextView scoreDisplay = (TextView) convertView.findViewById(R.id.meme_score);
            int score = 0; // TODO: Replace with score from API when available
            scoreDisplay.setText("+" + score);
            View voteButton = convertView.findViewById(R.id.meme_vote_button);
            voteButton.setOnClickListener(this);
            voteButton.setTag(position);
        }
        return convertView;
    }

    @Override
    public void onClick(View view) {
        Integer position = (Integer) view.getTag();
        CloudMemeMeme meme = getItem(position);
        if (mVoteListener != null && meme.getId() != null) {
            mVoteListener.registerVote(meme.getId());
        }
    }
}
