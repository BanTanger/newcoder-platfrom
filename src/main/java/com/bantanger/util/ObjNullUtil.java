package com.bantanger.util;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

/**
 * @author bantanger 半糖
 * @version 1.0
 * @Description
 * @Date 2022/8/19 18:33
 */

public class ObjNullUtil {
    public static boolean objCheckIsNull(Object object) {
        Class clazz = (Class) object.getClass(); // 得到类对象
        Field fields[] = clazz.getDeclaredFields(); // 得到所有属性
        boolean flag = true; //定义返回结果，默认为true
        for (Field field : fields) {
            field.setAccessible(true);
            Object fieldValue = null;
            try {
                fieldValue = field.get(object); //得到属性值
                Type fieldType = field.getGenericType();//得到属性类型
                String fieldName = field.getName(); // 得到属性名
                System.out.println("属性类型：" + fieldType + ",属性名：" + fieldName + ",属性值：" + fieldValue);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            //只要有一个属性值不为null 就返回false 表示对象不为null
            if (fieldValue != null) {
                flag = false;
                break;
            }
        }
        return flag;
    }
}

