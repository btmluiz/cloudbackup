package dev.nardole.cloudbackup.config;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Deprecated(since = "1.0.7")
@Retention(RetentionPolicy.RUNTIME)
public @interface FileName {
    String value();
}
