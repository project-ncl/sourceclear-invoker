package com.redhat.engineering.srcclr.json.securitydata;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
"cvss3_base_score",
"cvss3_scoring_vector",
"status"
})
public class Cvss3 {

    @JsonProperty("cvss3_base_score")
    private String cvss3BaseScore;
    @JsonProperty("cvss3_scoring_vector")
    private String cvss3ScoringVector;
    @JsonProperty("status")
    private String status;

    @JsonProperty("cvss3_base_score")
    public String getCvss3BaseScore() {
    return cvss3BaseScore;
    }

    @JsonProperty("cvss3_base_score")
    public void setCvss3BaseScore(String cvss3BaseScore) {
    this.cvss3BaseScore = cvss3BaseScore;
    }

    @JsonProperty("cvss3_scoring_vector")
    public String getCvss3ScoringVector() {
    return cvss3ScoringVector;
    }

    @JsonProperty("cvss3_scoring_vector")
    public void setCvss3ScoringVector(String cvss3ScoringVector) {
    this.cvss3ScoringVector = cvss3ScoringVector;
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
    return new ToStringBuilder(this).append("cvss3BaseScore", cvss3BaseScore).append("cvss3ScoringVector", cvss3ScoringVector).append("status", status).toString();
    }

}