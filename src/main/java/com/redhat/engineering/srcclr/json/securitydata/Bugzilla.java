package com.redhat.engineering.srcclr.json.securitydata;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.builder.ToStringBuilder;

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
    return new ToStringBuilder(this).append("description", description).append("id", id).append("url", url).toString();
    }

}