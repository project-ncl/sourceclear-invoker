package com.redhat.engineering.srcclr.notification;

import com.redhat.engineering.srcclr.SrcClrWrapper;
import com.redhat.engineering.srcclr.processor.ProcessorResult;

import java.util.Set;

public interface Notifier
{
    void notify( SrcClrWrapper parent, String scanInfo, Set<ProcessorResult> v );
}
