package com.redhat.engineering.srcclr.notification;

import com.redhat.engineering.srcclr.SrcClrWrapper;
import com.redhat.engineering.srcclr.json.sourceclear.Vulnerability;

import java.util.Set;

public interface Notifier
{
    void notify( SrcClrWrapper parent, Set<Vulnerability> v );
}
