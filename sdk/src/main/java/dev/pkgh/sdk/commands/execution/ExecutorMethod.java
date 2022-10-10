package dev.pkgh.sdk.commands.execution;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <br>Основной экзекутор команды.
 * <br>
 * <br>Вызывается, когда подкоманда не найдена или
 * <br>массив аргументов пуст
 * <br>
 * <br>См. также: {@link SubcommandExecutor}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ExecutorMethod {

}
