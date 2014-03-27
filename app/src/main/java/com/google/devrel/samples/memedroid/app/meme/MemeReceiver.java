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

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

/**
 * ResultReceiver subclass and listener interface, used in the communication between the
 * activities and the Memedroid API to handle the result from writes. Proxies between the
 * intentservice and the calling activity.
 */
public class MemeReceiver extends ResultReceiver {
    private MemeListener mListener;

    /**
     * Create a new MemeReceiver.
     *
     * @param handler
     */
    public MemeReceiver(Handler handler) {
        super(handler);
    }

    /**
     * Set the object which will be called when the result is received.
     *
     * @param listener
     */
    public void setListener(MemeListener listener) {
        mListener = listener;
    }

    /**
     * Callback when the result is received from the IntentService
     *
     * @param resultCode
     * @param resultData
     */
    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if (mListener != null) {
            mListener.onReceiveResult(resultCode, resultData);
        }
    }

    /**
     * Interface for objects which are interested in the result from write calls to the
     * Memedroid API.
     */
    public interface MemeListener {
        public void onReceiveResult(int resultCode, Bundle resultData);
    }
}
