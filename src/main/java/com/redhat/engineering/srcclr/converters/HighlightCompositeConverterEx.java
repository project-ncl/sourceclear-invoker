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
