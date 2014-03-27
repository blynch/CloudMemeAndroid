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


import com.appspot.cloudmemebackend.cloudmeme.model.CloudMemeTemplate;
import com.google.devrel.samples.memedroid.app.meme.VolleyContainer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.toolbox.NetworkImageView;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass for displaying meme templates.
 */
public class MemeTemplateFragment extends Fragment {
    private static final String TAG = "MemeDroid-MemeTemplateFragment";

    private CloudMemeTemplate mTemplate;
    private VolleyContainer mVolley;

    /**
     * Required public constructor. Prefer using newInstance where possible.
     */
    public MemeTemplateFragment() {
        mVolley = VolleyContainer.getInstance(getActivity());
    }

    /**
     * Create a new MemeTemplateFragment with a given template.
     *
     * @param template
     * @return the new instance
     */
    public static MemeTemplateFragment newInstance(CloudMemeTemplate template) {
        MemeTemplateFragment frag = new MemeTemplateFragment();
        frag.setTemplate(template);
        return frag;
    }

    /**
     * The fragment displays a template image - this method sets the template it uses.
     *
     * @param template
     */
    public void setTemplate(CloudMemeTemplate template) {
        mTemplate = template;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_meme, container, false);
        if (mTemplate != null && v != null) {
            NetworkImageView image = (NetworkImageView) v.findViewById(R.id.meme_template_image);
            image.setImageUrl(mTemplate.getUrl(), mVolley.getImageLoader());
            image.setDefaultImageResId(R.drawable.ic_action_camera_wide);
            image.setErrorImageResId(R.drawable.ic_action_camera_wide);
        }
        return v;
    }


}
