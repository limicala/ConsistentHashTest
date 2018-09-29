package annotation.controller;

import util.ClassUtil;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Test {
    public static void main(String[] args) throws Exception {

        // 扫描
        Set<Class<?>> classSet = ClassUtil.getClassSet("annotation.controller");
        Map<String, Action> actionMap = new HashMap<String, Action>();
        for(Class<?> clazz : classSet) {
            Controller controllerAnno = (Controller) clazz.getAnnotation(Controller.class );

            if (controllerAnno != null) {
                Object object = clazz.newInstance();
                RequestUrl prefix = (RequestUrl) clazz.getAnnotation(RequestUrl.class);
                String pre = "/";
                if (prefix != null) pre = prefix.value();

                for(Method method : clazz.getMethods()){

                    RequestUrl requestUrl = (RequestUrl) method.getAnnotation(RequestUrl.class);

                    if (requestUrl != null){
                        System.out.println("Method Name : " + method.getName());
                        String url = requestUrl.value();

                        // 存入Action
                        String finalUrl = pre + "/" + url;
                        Action action = new Action(method, object);

                        actionMap.put(finalUrl, action);
                    }

                }
            }


        }

        Set<String> set = actionMap.keySet();
        for(String s : set){
            actionMap.get(s).invokeMethod();
        }
        // 接收请求并调用Action的方法
    }
}

@Controller
@RequestUrl("/user")
class UserController {

    @RequestUrl("login.do")
    public void login(){
        System.out.println("Login");
    }

    @RequestUrl("aView.do")
    public void aaa(){
        System.out.println("aaa");
    }
}

@Controller
class IndexController {

    @RequestUrl("aa.do")
    public void aa(){
        System.out.println("aa");
    }

    @RequestUrl("cc")
    public void bb(){
        System.out.println("bb");
    }
}