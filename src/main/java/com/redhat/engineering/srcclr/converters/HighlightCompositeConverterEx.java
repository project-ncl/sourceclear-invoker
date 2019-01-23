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
package com.redhat.engineering.srcclr.converters;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.pattern.color.ANSIConstants;
import ch.qos.logback.core.pattern.color.ForegroundCompositeConverterBase;

/**
 * Source: https://github.com/shuwada/logback-custom-color
 */
public class HighlightCompositeConverterEx
                extends ForegroundCompositeConverterBase<ILoggingEvent>
{
    /**
     * Derived classes return the foreground color specific to the derived class instance.
     * @return the foreground color for this instance
     * @param event the logging event passed in.
     */
    @Override
    protected String getForegroundColorCode( ILoggingEvent event) {
        Level level = event.getLevel();
        switch (level.toInt()) {
            case Level.ERROR_INT:
                return ANSIConstants.BOLD + ANSIConstants.RED_FG; // same as default color scheme
            case Level.WARN_INT:
                return ANSIConstants.RED_FG;// same as default color scheme
            case Level.INFO_INT:
                return ANSIConstants.CYAN_FG; // use CYAN instead of BLUE
            default:
                return ANSIConstants.MAGENTA_FG;
        }
    }
}
