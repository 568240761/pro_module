package com.ly.pub.util

import java.lang.reflect.*

/**
 * Created by LanYang on 2019/1/26
 * 反射工具类
 */

/**
 * 获得定义Class时声明的父类的泛型参数类型
 *
 * @param clazz 子类对应的Class对象
 * @param index 子类继承父类时传入的泛型的索引,从0开始
 * @return 泛型参数类型
 */
fun getSuperClassGenericType(clazz: Class<*>, index: Int): Class<*> {

    val genType = clazz.genericSuperclass as? ParameterizedType ?: return Any::class.java

    val params = genType.actualTypeArguments

    if (index >= params.size || index < 0) {
        return Any::class.java
    }

    return if (params[index] !is Class<*>) {
        Any::class.java
    } else params[index] as Class<*>

}


/**
 * 使filed变为可访问
 *
 * @param field 字段
 */
fun makeAccessible(field: Field) {

    if (!Modifier.isPublic(field.modifiers)) {
        field.isAccessible = true
    }

}

/**
 * 循环向上转型, 获取对象的 DeclaredField
 *
 * @param object    对象实例
 * @param filedName 字段名称
 * @return 字段实例
 */
fun getDeclaredField(`object`: Any, filedName: String): Field? {

    var superClass: Class<*>? = `object`.javaClass
    while (superClass != Any::class.java) {
        try {
            return superClass?.getDeclaredField(filedName)
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        }

        superClass = superClass!!.superclass
    }
    return null

}

/**
 * 直接设置对象属性值,忽略private/protected修饰符
 *
 * @param object    对象实例
 * @param fieldName 字段名称
 * @param value     字段值
 * @throws IllegalArgumentException 没有找到该字段时,抛出该异常
 */
fun setFieldValue(`object`: Any, fieldName: String, value: Any) {

    val field =
        getDeclaredField(`object`, fieldName) ?: throw IllegalArgumentException("在[$`object`]中没有找到字段[$fieldName]")

    makeAccessible(field)

    try {
        field.set(`object`, value)
    } catch (e: IllegalAccessException) {
        e.printStackTrace()
    }

}

/**
 * 直接读取对象的属性值,忽略private/protected修饰符
 *
 * @param object    对象实例
 * @param fieldName 字段名称
 * @return 该字段的值
 * @throws IllegalArgumentException 没有找到该字段时,抛出该异常
 */
fun getFieldValue(`object`: Any, fieldName: String): Any? {

    val field =
        getDeclaredField(`object`, fieldName) ?: throw IllegalArgumentException("在[$`object`]中没有找到字段[$fieldName]")

    makeAccessible(field)

    var result: Any? = null

    try {
        result = field.get(`object`)
    } catch (e: IllegalAccessException) {
        e.printStackTrace()
    }

    return result

}

/**
 * 循环向上转型, 获取对象的DeclaredMethod
 *
 * @param object         对象实例
 * @param methodName     方法名称
 * @param parameterTypes 方法参数类型
 * @return 方法实例
 */
fun getDeclaredMethod(
    `object`: Any,
    methodName: String,
    parameterTypes: Array<Class<*>>
): Method? {

    var superClass: Class<*>? = `object`.javaClass
    while (superClass != Any::class.java) {
        try {
            return superClass?.getDeclaredMethod(methodName, *parameterTypes)
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        }

        superClass = superClass!!.superclass
    }

    return null
}

/**
 * 直接调用对象方法, 而忽略修饰符(private, protected)
 *
 * @param object         对象实例
 * @param methodName     方法名称
 * @param parameterTypes 方法参数类型
 * @param parameters     方法参数值
 * @return 对象方法执行后的结果
 * @throws IllegalArgumentException 没有找到该方法时,抛出该异常
 */
fun invokeMethod(
    `object`: Any,
    methodName: String,
    parameterTypes: Array<Class<*>>,
    parameters: Array<Any>
): Any? {

    val method = getDeclaredMethod(`object`, methodName, parameterTypes)
        ?: throw IllegalArgumentException("在[$`object`]中没有找到方法[$methodName]")

    method.isAccessible = true

    try {
        return method.invoke(`object`, *parameters)
    } catch (e: IllegalAccessException) {
        e.printStackTrace()
    } catch (e: InvocationTargetException) {
        e.printStackTrace()
    }

    return null
}

/**
 * 获取该类的所有父类型
 * @param clazz 该类型
 * @return 所有父类型的集合,除了[Object]
 */
fun getAllSuperClass(clazz: Class<*>): ArrayList<Class<*>> {
    val list = ArrayList<Class<*>>()
    var superClass = clazz.superclass
    while (superClass != null) {
        if (superClass.name == "java.lang.Object") break
        list.add(superClass)
        superClass = superClass.superclass
    }
    return list
}

