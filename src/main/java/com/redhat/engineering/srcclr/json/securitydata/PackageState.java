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
"cpe",
"fix_state",
"package_name",
"product_name"
})
public class PackageState {

    @JsonProperty("cpe")
    private String cpe;
    @JsonProperty("fix_state")
    private String fixState;
    @JsonProperty("package_name")
    private String packageName;
    @JsonProperty("product_name")
    private String productName;

    @JsonProperty("cpe")
    public String getCpe() {
    return cpe;
    }

    @JsonProperty("cpe")
    public void setCpe(String cpe) {
    this.cpe = cpe;
    }

    @JsonProperty("fix_state")
    public String getFixState() {
    return fixState;
    }

    @JsonProperty("fix_state")
    public void setFixState(String fixState) {
    this.fixState = fixState;
    }

    @JsonProperty("package_name")
    public String getPackageName() {
    return packageName;
    }

    @JsonProperty("package_name")
    public void setPackageName(String packageName) {
    this.packageName = packageName;
    }

    @JsonProperty("product_name")
    public String getProductName() {
    return productName;
    }

    @JsonProperty("product_name")
    public void setProductName(String productName) {
    this.productName = productName;
    }

    @Override
    public String toString() {
    return new ToStringBuilder(this, STYLE).append("cpe", cpe).append("fixState", fixState).append("packageName", packageName).append("productName", productName).toString();
    }

}
