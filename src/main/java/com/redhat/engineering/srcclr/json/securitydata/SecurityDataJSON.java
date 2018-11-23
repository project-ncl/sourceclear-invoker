package com.redhat.engineering.srcclr.json.securitydata;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
"acknowledgement",
"affected_release",
"bugzilla",
"cvss",
"cvss3",
"details",
"name",
"package_state",
"public_date",
"statement",
"threat_severity"
})
public class SecurityDataJSON {

    @JsonProperty("acknowledgement")
    private String acknowledgement;
    @JsonProperty("affected_release")
    private List<AffectedRelease> affectedRelease = null;
    @JsonProperty("bugzilla")
    private Bugzilla bugzilla;
    @JsonProperty("cvss")
    private Cvss cvss;
    @JsonProperty("cvss3")
    private Cvss3 cvss3;
    @JsonProperty("details")
    private List<String> details = null;
    @JsonProperty("name")
    private String name;
    @JsonProperty("package_state")
    private List<PackageState> packageState = null;
    @JsonProperty("public_date")
    private String publicDate;
    @JsonProperty("statement")
    private String statement;
    @JsonProperty("threat_severity")
    private String threatSeverity;

    @JsonProperty("acknowledgement")
    public String getAcknowledgement() {
    return acknowledgement;
    }

    @JsonProperty("acknowledgement")
    public void setAcknowledgement(String acknowledgement) {
    this.acknowledgement = acknowledgement;
    }

    @JsonProperty("affected_release")
    public List<AffectedRelease> getAffectedRelease() {
    return affectedRelease;
    }

    @JsonProperty("affected_release")
    public void setAffectedRelease(List<AffectedRelease> affectedRelease) {
    this.affectedRelease = affectedRelease;
    }

    @JsonProperty("bugzilla")
    public Bugzilla getBugzilla() {
    return bugzilla;
    }

    @JsonProperty("bugzilla")
    public void setBugzilla(Bugzilla bugzilla) {
    this.bugzilla = bugzilla;
    }

    @JsonProperty("cvss")
    public Cvss getCvss() {
    return cvss;
    }

    @JsonProperty("cvss")
    public void setCvss(Cvss cvss) {
    this.cvss = cvss;
    }

    @JsonProperty("cvss3")
    public Cvss3 getCvss3() {
    return cvss3;
    }

    @JsonProperty("cvss3")
    public void setCvss3(Cvss3 cvss3) {
    this.cvss3 = cvss3;
    }

    @JsonProperty("details")
    public List<String> getDetails() {
    return details;
    }

    @JsonProperty("details")
    public void setDetails(List<String> details) {
    this.details = details;
    }

    @JsonProperty("name")
    public String getName() {
    return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
    this.name = name;
    }

    @JsonProperty("package_state")
    public List<PackageState> getPackageState() {
    return packageState;
    }

    @JsonProperty("package_state")
    public void setPackageState(List<PackageState> packageState) {
    this.packageState = packageState;
    }

    @JsonProperty("public_date")
    public String getPublicDate() {
    return publicDate;
    }

    @JsonProperty("public_date")
    public void setPublicDate(String publicDate) {
    this.publicDate = publicDate;
    }

    @JsonProperty("statement")
    public String getStatement() {
    return statement;
    }

    @JsonProperty("statement")
    public void setStatement(String statement) {
    this.statement = statement;
    }

    @JsonProperty("threat_severity")
    public String getThreatSeverity() {
    return threatSeverity;
    }

    @JsonProperty("threat_severity")
    public void setThreatSeverity(String threatSeverity) {
    this.threatSeverity = threatSeverity;
    }

    @Override
    public String toString() {
    return new ToStringBuilder(this).append("acknowledgement", acknowledgement).append("affectedRelease", affectedRelease).append("bugzilla", bugzilla).append("cvss", cvss).append("cvss3", cvss3).append("details", details).append("name", name).append("packageState", packageState).append("publicDate", publicDate).append("statement", statement).append("threatSeverity", threatSeverity).toString();
    }

}