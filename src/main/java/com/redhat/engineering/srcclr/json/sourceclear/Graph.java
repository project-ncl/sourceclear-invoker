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

import java.util.ArrayList;
import java.util.List;

import static com.redhat.engineering.srcclr.utils.Style.STYLE;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "coords",
    "directs",
    "filename",
    "lineNumber",
    "moduleName",
    "sha1",
    "sha2",
    "bytecodeHash"
})
public class Graph {

    @JsonProperty("coords")
    private Object coords;
    @JsonProperty("directs")
    private List<Object> directs = new ArrayList<Object>();
    @JsonProperty("filename")
    private String filename;
    @JsonProperty("lineNumber")
    private Object lineNumber;
    @JsonProperty("moduleName")
    private Object moduleName;
    @JsonProperty("sha1")
    private String sha1;
    @JsonProperty("sha2")
    private String sha2;
    @JsonProperty("bytecodeHash")
    private String bytecodeHash;

    @JsonProperty("coords")
    public Object getCoords() {
        return coords;
    }

    @JsonProperty("coords")
    public void setCoords(Object coords) {
        this.coords = coords;
    }

    @JsonProperty("directs")
    public List<Object> getDirects() {
        return directs;
    }

    @JsonProperty("directs")
    public void setDirects(List<Object> directs) {
        this.directs = directs;
    }

    @JsonProperty("filename")
    public String getFilename() {
        return filename;
    }

    @JsonProperty("filename")
    public void setFilename(String filename) {
        this.filename = filename;
    }

    @JsonProperty("lineNumber")
    public Object getLineNumber() {
        return lineNumber;
    }

    @JsonProperty("lineNumber")
    public void setLineNumber(Object lineNumber) {
        this.lineNumber = lineNumber;
    }

    @JsonProperty("moduleName")
    public Object getModuleName() {
        return moduleName;
    }

    @JsonProperty("moduleName")
    public void setModuleName(Object moduleName) {
        this.moduleName = moduleName;
    }

    @JsonProperty("sha1")
    public String getSha1() {
        return sha1;
    }

    @JsonProperty("sha1")
    public void setSha1(String sha1) {
        this.sha1 = sha1;
    }

    @JsonProperty("sha2")
    public String getSha2() {
        return sha2;
    }

    @JsonProperty("sha2")
    public void setSha2(String sha2) {
        this.sha2 = sha2;
    }

    @JsonProperty("bytecodeHash")
    public String getBytecodeHash() {
        return bytecodeHash;
    }

    @JsonProperty("bytecodeHash")
    public void setBytecodeHash(String bytecodeHash) {
        this.bytecodeHash = bytecodeHash;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, STYLE).append("coords", coords).append("directs", directs).append("filename", filename).append("lineNumber", lineNumber).append("moduleName", moduleName).append("sha1", sha1).append("sha2", sha2).append("bytecodeHash", bytecodeHash).toString();
    }

}
