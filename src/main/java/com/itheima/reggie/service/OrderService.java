package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.entity.Orders;
import com.itheima.reggie.Dto.OrdersDto;


public interface OrderService extends IService<Orders> {
    void submit(Orders orders);
    Page<OrdersDto> userPage(int page,int pageSize);
    void again(Orders order);
}
