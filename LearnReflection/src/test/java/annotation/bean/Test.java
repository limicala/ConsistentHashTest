package annotation.bean;

import java.lang.reflect.Field;

public class Test {

    public static void main(String[] args) throws Exception{
        Class clazz = UserService.class;

        // 新建一个实例对象
        Object object = clazz.newInstance();

        // 获取该类定义的所有属性
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields){
            System.out.println(field);
        }
        Field field = fields[0];
        // 设置写入权限
        field.setAccessible(true);

        // 给userMapper属性指向一个实例对象
        field.set(object, new UserMapperImpl());

        // 调用test方法测试结果
        clazz.getMethod("test").invoke(object);
    }
}


class UserService {

    @AutoLine
    private UserMapper userMapper;


    public void test(){
        userMapper.sayHello();
    }
}