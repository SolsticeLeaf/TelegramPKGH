package xyz.winston.impl;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface Impl {

    Class<?> value();
    ImplPriority priority() default ImplPriority.NORMAL;

}
