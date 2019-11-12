package com.redhat.engineering.srcclr.utils;

import org.apache.commons.lang3.builder.MultilineRecursiveToStringStyle;

public final class Style extends MultilineRecursiveToStringStyle
{
    public static final Style STYLE = new Style();

    private Style()
    {
        super();
        setUseIdentityHashCode( false );
    }
}
