/*
 * Copyright (C) 2018 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.redhat.engineering.srcclr.json.securitydata;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import static com.redhat.engineering.srcclr.utils.Style.STYLE;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
"description",
"id",
"url"
})
public class Bugzilla {

    @JsonProperty("description")
    private String description;
    @JsonProperty("id")
    private String id;
    @JsonProperty("url")
    private String url;

    @JsonProperty("description")
    public String getDescription() {
    return description;
    }

    @JsonProperty("description")
    public void setDescription(String description) {
    this.description = description;
    }

    @JsonProperty("id")
    public String getId() {
    return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
    this.id = id;
    }

    @JsonProperty("url")
    public String getUrl() {
    return url;
    }

    @JsonProperty("url")
    public void setUrl(String url) {
    this.url = url;
    }

    @Override
    public String toString() {
    return new ToStringBuilder(this, STYLE).append("description", description).append("id", id).append("url", url).toString();
    }

}
