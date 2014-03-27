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
import com.google.devrel.samples.memedroid.app.meme.MemeTemplateLoader;

import android.content.res.Configuration;
import android.support.v4.app.LoaderManager;
import android.content.Intent;
import android.support.v4.content.Loader;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Activity to provide the UI for creating a new Meme. Returns the meme
 * details as the activity response.
 */
public class CreateActivity extends FragmentActivity implements
        LoaderManager.LoaderCallbacks<List<CloudMemeTemplate>>, TextWatcher,
        ViewPager.OnPageChangeListener, AdapterView.OnItemClickListener {

    private static final int MEME_TEMPLATE_LIST = 44221;

    private static final String STATE_TEXT = "hasText";
    private static final String TAG = "MemeDroid-CreateActivity";

    private List<CloudMemeTemplate> mTemplates;
    private View mCurrentView;
    private TemplateAdapter mAdapter;
    private Boolean mHasText = false;
    private int mCurrentEntry = 0;
    private Handler mHandler;
    private final Runnable mRefreshTask = new Runnable() {
        @Override
        public void run() {
            setProgressBarIndeterminateVisibility(true);
            getSupportLoaderManager().restartLoader(MEME_TEMPLATE_LIST, null, CreateActivity.this);
        }
    };
    // Scaling factor for the refresh time.
    private int mBackOff = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.translate_in_top, R.anim.translate_out_top);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_create);
        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        mHandler = new Handler();

        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        GridView gridView = (GridView) findViewById(R.id.template_grid);
        if (viewPager != null) {
            PagerAdapter adapter =  new MemeTemplatePagerAdapter(getSupportFragmentManager());
            viewPager.setAdapter(adapter);
            mAdapter = (TemplateAdapter) adapter;
            viewPager.setOffscreenPageLimit(3);
            viewPager.setPageMargin(-54);
            viewPager.setOnPageChangeListener(this);
            mCurrentView = viewPager;
            findViewById(R.id.meme_template_number).setVisibility(View.VISIBLE);
        } else if (gridView != null) {
            ListAdapter adapter = new MemeTemplateAdapter(this, R.layout.fragment_meme);
            gridView.setAdapter(adapter);
            mAdapter = (TemplateAdapter) adapter;
            mCurrentView = gridView;
            gridView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
            gridView.setOnItemClickListener(this);
        } else {
            throw new IllegalStateException("No valid layout found.");
        }
        getSupportLoaderManager().initLoader(MEME_TEMPLATE_LIST, null, this);
        if (savedInstanceState != null) {
            mHasText = savedInstanceState.getBoolean(STATE_TEXT, false);
            getSupportLoaderManager().restartLoader(MEME_TEMPLATE_LIST, null, CreateActivity.this);
        }
        EditText et = (EditText) findViewById(R.id.top_text);
        et.addTextChangedListener(this);
        setProgressBarIndeterminateVisibility(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.create, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.menu_create);
        if (item != null) {
            item.setEnabled(mHasText);
            if (item.getIcon() != null) {
                item.getIcon().setAlpha(mHasText ? 255 : 100);
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(STATE_TEXT, mHasText);
        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<List<CloudMemeTemplate>> onCreateLoader(int i, Bundle bundle) {
        if (i == MEME_TEMPLATE_LIST) {
            return new MemeTemplateLoader(this);
        }
        return null;
    }

    @Override
    public void onLoadFinished(
            Loader<List<CloudMemeTemplate>> objectLoader, List<CloudMemeTemplate> memes) {
        mTemplates = memes;
        mAdapter.setData(mTemplates);
        if (memes != null && memes.size() > 0) {
            mCurrentView.setVisibility(View.VISIBLE);
            findViewById(R.id.meme_template_empty).setVisibility(View.GONE);
        } else {
            mCurrentView.setVisibility(View.GONE);
            findViewById(R.id.meme_template_empty).setVisibility(View.VISIBLE);
            mHandler.postDelayed(mRefreshTask, mBackOff * Constants.BLANK_REFRESH_MILLISECONDS);
            if (mBackOff < Constants.MAX_BACKOFF) {
                mBackOff *= 2;
            }
        }
        setProgressBarIndeterminateVisibility(false);
    }

    @Override
    public void onLoaderReset(Loader<List<CloudMemeTemplate>> objectLoader) {
        // NOP,
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_create) {
            Intent result = new Intent();
            String im = mTemplates.get(mCurrentEntry).getUrl();
            result.putExtra(Constants.MEME_IMAGE_URL, im);
            String text = ((EditText) findViewById(R.id.top_text)).getText().toString();
            result.putExtra(Constants.MEME_TEXT, text);
            setResult(RESULT_OK, result);
            finish();
        } else if (id == android.R.id.home) {
            setResult(RESULT_CANCELED);
            finish();
        }
        return true;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        // NOP.
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
        if (mHasText != (count > 0)) {
            // Trigger enabling the create button.
            mHasText = count > 0;
            invalidateOptionsMenu();
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {
        // NOP.
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (mTemplates != null) {
            updatePagerText(position, mTemplates.size());
        }
    }

    @Override
    public void onPageSelected(int position) {
        mCurrentEntry = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        // NOP.
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        mAdapter.setSelectedItem(position);
        mCurrentEntry = position;
    }

    /**
     * Helper to update the pager display on portrait phone layouts.
     * @param page current page number
     * @param of total pages
     */
    private void updatePagerText(int page, int of) {
        page += 1; // zero indexed.
        ((TextView) findViewById(R.id.meme_template_number)).setText(page + "/" + of);
    }
}
