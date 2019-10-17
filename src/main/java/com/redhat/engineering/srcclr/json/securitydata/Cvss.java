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
"cvss_base_score",
"cvss_scoring_vector",
"status"
})
public class Cvss {

    @JsonProperty("cvss_base_score")
    private String cvssBaseScore;
    @JsonProperty("cvss_scoring_vector")
    private String cvssScoringVector;
    @JsonProperty("status")
    private String status;

    @JsonProperty("cvss_base_score")
    public String getCvssBaseScore() {
    return cvssBaseScore;
    }

    @JsonProperty("cvss_base_score")
    public void setCvssBaseScore(String cvssBaseScore) {
    this.cvssBaseScore = cvssBaseScore;
    }

    @JsonProperty("cvss_scoring_vector")
    public String getCvssScoringVector() {
    return cvssScoringVector;
    }

    @JsonProperty("cvss_scoring_vector")
    public void setCvssScoringVector(String cvssScoringVector) {
    this.cvssScoringVector = cvssScoringVector;
    }

    @JsonProperty("status")
    public String getStatus() {
    return status;
    }

    @JsonProperty("status")
    public void setStatus(String status) {
    this.status = status;
    }

    @Override
    public String toString() {
    return new ToStringBuilder(this, STYLE).append("cvssBaseScore", cvssBaseScore).append("cvssScoringVector", cvssScoringVector).append("status", status).toString();
    }

}
