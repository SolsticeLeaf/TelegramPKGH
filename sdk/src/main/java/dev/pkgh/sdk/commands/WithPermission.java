package dev.pkgh.sdk.commands;

import dev.pkgh.sdk.type.UserPermission;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author winston
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface WithPermission {

    /**
     * Permission that user must have to invoke this command
     */
    UserPermission value();

}
