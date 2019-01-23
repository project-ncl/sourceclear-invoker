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
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
"advisory",
"cpe",
"product_name",
"release_date",
"package"
})
public class AffectedRelease {

    @JsonProperty("advisory")
    private String advisory;
    @JsonProperty("cpe")
    private String cpe;
    @JsonProperty("product_name")
    private String productName;
    @JsonProperty("release_date")
    private String releaseDate;
    @JsonProperty("package")
    private String _package;

    @JsonProperty("advisory")
    public String getAdvisory() {
    return advisory;
    }

    @JsonProperty("advisory")
    public void setAdvisory(String advisory) {
    this.advisory = advisory;
    }

    @JsonProperty("cpe")
    public String getCpe() {
    return cpe;
    }

    @JsonProperty("cpe")
    public void setCpe(String cpe) {
    this.cpe = cpe;
    }

    @JsonProperty("product_name")
    public String getProductName() {
    return productName;
    }

    @JsonProperty("product_name")
    public void setProductName(String productName) {
    this.productName = productName;
    }

    @JsonProperty("release_date")
    public String getReleaseDate() {
    return releaseDate;
    }

    @JsonProperty("release_date")
    public void setReleaseDate(String releaseDate) {
    this.releaseDate = releaseDate;
    }

    @JsonProperty("package")
    public String getPackage() {
    return _package;
    }

    @JsonProperty("package")
    public void setPackage(String _package) {
    this._package = _package;
    }

    @Override
    public String toString() {
    return new ToStringBuilder(this).append("advisory", advisory).append("cpe", cpe).append("productName", productName).append("releaseDate", releaseDate).append("_package", _package).toString();
    }

}