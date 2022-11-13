package com.itheima.reggie.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.itheima.reggie.Utils.BaseContextUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;


import java.time.LocalDateTime;
@Component
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime",LocalDateTime.now());
        metaObject.setValue("createUser", BaseContextUtils.getCurrentId());
        metaObject.setValue("updateUser",BaseContextUtils.getCurrentId());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        long id = Thread.currentThread().getId();
        log.info("当前线程id{}",id);
        metaObject.setValue("updateTime",LocalDateTime.now());
        metaObject.setValue("updateUser",BaseContextUtils.getCurrentId());
    }
}
