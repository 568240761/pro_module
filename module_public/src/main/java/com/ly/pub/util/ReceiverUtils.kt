package com.ly.pub.util

/**
 * Created by LanYang on 2018/8/17
 * 多个页面之间的交互
 */

private var set = HashSet<Receiver>()

fun registerReceivers(receiver: Receiver) {
    set.add(receiver)
}

fun removeReceivers(receiver: Receiver) {
    set.remove(receiver)
}


/**
 * @param type 类型
 * @param any 数据对象
 */
fun notifyReceivers(
    type: String,
    any: Any? = null
) {
    for (bean in set) {
        bean.receive(type, any)
    }
}

interface Receiver {
    /**
     * @param type 类型
     * @param any 数据对象
     */
    fun receive(type: String, any: Any? = null) {}
}

