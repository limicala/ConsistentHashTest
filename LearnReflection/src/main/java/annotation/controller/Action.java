package annotation.controller;

import java.lang.reflect.Method;

public class Action {
//    private final Class<?> controllerClass;
//    private final String url;
    private final Method method;
    private final Object object;


    public Action(Method method, Object object) {
//        this.url = url;
        this.method = method;
        this.object = object;
    }

    public void invokeMethod() throws Exception{
        method.invoke(object);
    }
}
