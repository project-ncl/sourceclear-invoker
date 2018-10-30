package com.redhat.engineering.srcclr.notification;

import com.redhat.engineering.srcclr.SrcClrWrapper;
import com.redhat.engineering.srcclr.json.Vulnerability;

public interface Notifier
{
    void notify( SrcClrWrapper parent, Vulnerability v );
}
