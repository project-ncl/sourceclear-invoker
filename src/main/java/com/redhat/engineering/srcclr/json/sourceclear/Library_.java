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
package com.redhat.engineering.srcclr.json.sourceclear;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringExclude;

import java.util.ArrayList;
import java.util.List;

import static com.redhat.engineering.srcclr.utils.Style.STYLE;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "details",
    "_links"
})
public class Library_ {

    @JsonProperty("details")
    private List<Detail> details = new ArrayList<Detail>();
    @JsonProperty("_links")
    // Not appending the links as that simply contains a cross-reference for the JSON stream.
    @ToStringExclude
    private Links__ links;

    @JsonProperty("details")
    public List<Detail> getDetails() {
        return details;
    }

    @JsonProperty("details")
    public void setDetails(List<Detail> details) {
        this.details = details;
    }

    @JsonProperty("_links")
    public Links__ getLinks() {
        return links;
    }

    @JsonProperty("_links")
    public void setLinks(Links__ links) {
        this.links = links;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, STYLE).append("details", details).append("links", links).toString();
    }
}
