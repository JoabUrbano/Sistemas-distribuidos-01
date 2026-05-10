package middleware.remoting.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Nome lógico da operação remota usado pelo despacho HTTP/reflexão.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RemoteOperation {
    /**
     * Nome da operação; se vazio, usa o nome do método em tempo de execução.
     */
    String value() default "";
}
