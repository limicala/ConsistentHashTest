package annotation.controller;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE}) // for the type of class
@Retention(RetentionPolicy.RUNTIME)
public @interface Controller {
    String value() default "";
}
