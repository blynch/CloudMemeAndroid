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
import com.google.devrel.samples.memedroid.app.meme.MemeLoader;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment to contain a stream of memes.
 */
public class StreamFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<List<CloudMemeMeme>> {

    private static final int MEME_LIST = 12305;
    private static final String TAG = "Memedroid-StreamFragment";
    private MemeAdapter mAdapter;
    private Handler mHandler;
    private String mLastImageUrl;
    private final Runnable mRefreshTask = new Runnable() {
        @Override
        public void run() {
            refreshLoad();
        }
    };
    // Scale factor for back off.
    private int mBackOff = 1;

    /**
     * Required public constructor. Prefer newInstance().
     */
    public StreamFragment() {
        // Required empty public constructor.
    }

    /**
     * Create a new stream fragment.
     * @return StreamFragment
     */
    public static StreamFragment newInstance() {
        StreamFragment fragment = new StreamFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getLoaderManager() != null) {
            getLoaderManager().initLoader(MEME_LIST, null, this);
        } else {
            Log.e(TAG, "Missing loader manager.");
        }
        Activity activity = getActivity();
        if (activity != null) {
            mAdapter = new MemeAdapter(activity, R.layout.meme, new ArrayList<CloudMemeMeme>());

            if (activity instanceof VoteListener) {
                mAdapter.setVoteListener((VoteListener) activity);
            }
        }
        setProgressBarIndeterminateVisibility(true);
        mHandler = new Handler();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        mHandler.removeCallbacks(mRefreshTask);
        super.onStop();
    }

    /**
     * Refresh the backing data on the stream. Used periodically or if there has been
     * an update.
     */
    public void refreshLoad() {
        setProgressBarIndeterminateVisibility(true);
        if (getLoaderManager() != null) {
            getLoaderManager().restartLoader(MEME_LIST, null, this);
        } else {
            Log.e(TAG, "Missing loader manager.");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_stream, container, false);
        if (v == null) {
            return null;
        }
        AbsListView list = (AbsListView) v.findViewById(R.id.meme_list);
        list.setAdapter(mAdapter);
        list.setEmptyView(v.findViewById(android.R.id.empty));
        return v;
    }

    @Override
    public Loader<List<CloudMemeMeme>> onCreateLoader(int i, Bundle bundle) {
        if (i == MEME_LIST) {
            return new MemeLoader(getActivity());
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<List<CloudMemeMeme>> objectLoader, List<CloudMemeMeme> memes) {
        // If we have a manual check and automatic, make sure no further is automatically run.
        mHandler.removeCallbacks(mRefreshTask);
        setProgressBarIndeterminateVisibility(false);
        if (memes != null && memes.size() == 0) {
            memes = null; // Treat empty list as null.
        }
        if (memes != null && mLastImageUrl != null) {
            if(mLastImageUrl.equals(memes.get(0).getImageUrl())) {
                // No need to update, the most recent is the same which we will take as
                // meaning there are no changes.
                return;
            }
        }
        mAdapter.clear();
        if (memes != null) {
            mAdapter.addAll(memes);
            mLastImageUrl = memes.get(0).getImageUrl();
            // Setup an automatic refresh.
            mHandler.postDelayed(mRefreshTask, Constants.REFRESH_MILLISECONDS);
        } else {
            mLastImageUrl = null;
            // Setup a shorter refresh.
            mHandler.postDelayed(mRefreshTask, mBackOff * Constants.BLANK_REFRESH_MILLISECONDS);
            if (mBackOff < Constants.MAX_BACKOFF) {
                mBackOff *= 2;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<List<CloudMemeMeme>> objectLoader) {
        mAdapter.clear();
    }

    /**
     * Display or hide the progress indicator on the parent activity if possible.
     * @param show true if indicator should be shown.
     */
    private void setProgressBarIndeterminateVisibility(boolean show) {
        if (getActivity() != null) {
            getActivity().setProgressBarIndeterminateVisibility(show);
        }
    }
}
