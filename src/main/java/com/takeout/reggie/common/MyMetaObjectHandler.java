package com.takeout.reggie.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 自定义元数据对象处理器
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    /**
     * 插入时自动填充
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        //MyMetaObjectHandler类中无法获取HttpSession对象，需要通过其他方式获取登录用户id
        //可以使用ThreadLocal，它是JDK中提供的一个类
        //在doFilter时调用ThreadLocal的set方法设置当前线程局部变量的值（用户id），在此处get就可以获取了
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("createUser", BaseContext.getCurrentId());
        metaObject.setValue("updateUser", BaseContext.getCurrentId());
    }

    /**
     * 更新时自动填充
     * @param metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("updateUser", BaseContext.getCurrentId());
    }
}
