package com.redhat.engineering.srcclr.json.securitydata;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.builder.ToStringBuilder;

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
    return new ToStringBuilder(this).append("cvssBaseScore", cvssBaseScore).append("cvssScoringVector", cvssScoringVector).append("status", status).toString();
    }

}