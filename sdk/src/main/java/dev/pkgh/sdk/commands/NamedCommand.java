package dev.pkgh.sdk.commands;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines command name
 *
 * @author winston
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface NamedCommand {

    /**
     * Primary command name
     */
    String value();

}
