package dev.pkgh.sdk.commands.execution;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <br>Subcommand executor
 * <br>
 * <br>See also: {@link ExecutorMethod}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SubcommandExecutor {

    /**
     * Name of subcommand
     */
    String value();

}
