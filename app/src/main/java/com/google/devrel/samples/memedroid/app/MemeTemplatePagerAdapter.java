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

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.appspot.cloudmemebackend.cloudmeme.model.CloudMemeTemplate;

import java.util.List;

/**
 * Pager backing store for Meme template images.
 */
public class MemeTemplatePagerAdapter extends FragmentStatePagerAdapter
        implements TemplateAdapter {
    private List<CloudMemeTemplate> mData;

    public MemeTemplatePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public void setData(List<CloudMemeTemplate> data) {
        mData = data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public MemeTemplateFragment getItem(int position) {
        return MemeTemplateFragment.newInstance(mData.get(position));
    }

    @Override
    public void setSelectedItem(int item) {
        // NOP.
    }
}
