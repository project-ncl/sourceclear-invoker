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
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "updateToVersion",
    "versionRange",
    "fixText",
    "patch"
})
public class Detail {

    @JsonProperty("updateToVersion")
    private String updateToVersion;
    @JsonProperty("versionRange")
    private String versionRange;
    @JsonProperty("fixText")
    private String fixText;
    @JsonProperty("patch")
    private String patch;

    @JsonProperty("updateToVersion")
    public String getUpdateToVersion() {
        return updateToVersion;
    }

    @JsonProperty("updateToVersion")
    public void setUpdateToVersion(String updateToVersion) {
        this.updateToVersion = updateToVersion;
    }

    @JsonProperty("versionRange")
    public String getVersionRange() {
        return versionRange;
    }

    @JsonProperty("versionRange")
    public void setVersionRange(String versionRange) {
        this.versionRange = versionRange;
    }

    @JsonProperty("fixText")
    public String getFixText() {
        return fixText;
    }

    @JsonProperty("fixText")
    public void setFixText(String fixText) {
        this.fixText = fixText;
    }

    @JsonProperty("patch")
    public String getPatch() {
        return patch;
    }

    @JsonProperty("patch")
    public void setPatch(String patch) {
        this.patch = patch;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("updateToVersion", updateToVersion).append("versionRange", versionRange).append("fixText", fixText).append("patch", patch).toString();
    }

}
