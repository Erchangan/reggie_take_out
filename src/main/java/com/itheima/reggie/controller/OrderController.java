package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.Dto.OrdersDto;
import com.itheima.reggie.Utils.BaseContextUtils;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Orders;
import com.itheima.reggie.service.OrderDetailService;
import com.itheima.reggie.service.OrderService;
import com.sun.org.apache.bcel.internal.generic.LMUL;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;

@RestController
@RequestMapping("/order")
@Slf4j
public class OrderController {
    @Resource
    private OrderService orderService;
    @Resource
    private OrderDetailService orderDetailService;

    /**
     * 结算
     *
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders) {
        orderService.submit(orders);
        return R.success("下单成功");
    }

    /**
     * 分页查询
     *
     * @param page
     * @param pageSize
     * @param number
     * @param beginTime
     * @param endTime
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String number,
                        @DateTimeFormat(pattern = "yyyy-mm-dd HH:mm:ss") Date beginTime,
                        @DateTimeFormat(pattern = "yyyy-mm-dd HH:mm:ss") Date endTime) {
        Page<Orders> pageInfo = new Page<>(page, pageSize);
        LambdaQueryWrapper<Orders> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(number != null, Orders::getNumber, number);
        if (beginTime != null) {
            lambdaQueryWrapper.between(Orders::getOrderTime, beginTime, endTime);
        }
        orderService.page(pageInfo, lambdaQueryWrapper);
        return R.success(pageInfo);
    }

    /**
     * 修改派送状态
     *
     * @param orders
     * @return
     */
    @PutMapping
    public R<String> updateOrder(@RequestBody Orders orders) {
        //构造条件构造器
        LambdaUpdateWrapper<Orders> updateWrapper = new LambdaUpdateWrapper<>();
        //添加过滤条件
        updateWrapper.eq(Orders::getId, orders.getId());
        updateWrapper.set(Orders::getStatus, orders.getStatus());
        orderService.update(updateWrapper);

        return R.success("订单派送成功");
    }

    /**
     * 历史订单
     *
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/userPage")
    public R<Page> userPage(int page, int pageSize) {
        Page<OrdersDto> ordersDtoPage = orderService.userPage(page, pageSize);
        return R.success(ordersDtoPage);
    }

    /**
     * 再来一单
     * @param order
     * @return
     */
    @PostMapping("/again")
    public R<String> again(@RequestBody Orders order) {
        orderService.again(order);
        return R.success("再来一单");
    }

}
