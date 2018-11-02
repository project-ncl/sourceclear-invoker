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
package com.redhat.engineering.srcclr.processor;

import com.redhat.engineering.srcclr.json.securitydata.SecurityDataJSON;

public class SecurityDataProcessorResult {
    private Boolean to_notify = false;
    private Boolean to_fail = false;
    private String message = "";
    private SecurityDataJSON json = null;

    public Boolean isToNotify() {
        return this.to_notify;
    }

    public void setToNotify(Boolean to_notify) {
        this.to_notify = to_notify;
    }

    public Boolean isToFail() {
        return this.to_fail;
    }

    public void setToFail(Boolean to_fail) {
        this.to_fail = to_fail;
    }

    public SecurityDataJSON getJSON() {
        return this.json;
    }

    public void setJSON(SecurityDataJSON json) {
        this.json = json;
    }
    
    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}