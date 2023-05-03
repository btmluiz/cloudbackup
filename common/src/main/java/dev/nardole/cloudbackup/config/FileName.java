package dev.nardole.cloudbackup.config;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface FileName {
    String value();
}
