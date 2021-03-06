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
/*
 * This code was generated by https://code.google.com/p/google-apis-client-generator/
 * (build: 2014-02-14 18:40:25 UTC)
 * on 2014-03-17 at 22:35:45 UTC 
 * Modify at your own risk.
 */

package com.appspot.cloudmemebackend.cloudmeme.model;

/**
 * Collection of starter templates.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the cloudmeme. For a detailed explanation see:
 * <a href="http://code.google.com/p/google-http-java-client/wiki/JSON">http://code.google.com/p/google-http-java-client/wiki/JSON</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class CloudMemeTemplateCollection extends com.google.api.client.json.GenericJson {

  /**
   * Starter template for creating memes.
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.util.List<CloudMemeTemplate> items;

  static {
    // hack to force ProGuard to consider CloudMemeTemplate used, since otherwise it would be stripped out
    // see http://code.google.com/p/google-api-java-client/issues/detail?id=528
    com.google.api.client.util.Data.nullOf(CloudMemeTemplate.class);
  }

  /**
   * Starter template for creating memes.
   * @return value or {@code null} for none
   */
  public java.util.List<CloudMemeTemplate> getItems() {
    return items;
  }

  /**
   * Starter template for creating memes.
   * @param items items or {@code null} for none
   */
  public CloudMemeTemplateCollection setItems(java.util.List<CloudMemeTemplate> items) {
    this.items = items;
    return this;
  }

  @Override
  public CloudMemeTemplateCollection set(String fieldName, Object value) {
    return (CloudMemeTemplateCollection) super.set(fieldName, value);
  }

  @Override
  public CloudMemeTemplateCollection clone() {
    return (CloudMemeTemplateCollection) super.clone();
  }

}
