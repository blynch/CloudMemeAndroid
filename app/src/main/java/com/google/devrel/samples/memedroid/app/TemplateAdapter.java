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

import java.util.List;

/**
 * Common interface for the template adapter types.
 */
public interface TemplateAdapter {
    /**
     * Set the data used by the template view adapter.
     *
     * @param data
     */
    public void setData(List<CloudMemeTemplate> data);

    /**
     * Set that a given item has been selected.
     *
     * @param item
     */
    public void setSelectedItem(int item);
}
