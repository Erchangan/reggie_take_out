package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.Dto.OrdersDto;
import com.itheima.reggie.Utils.BaseContextUtils;
import com.itheima.reggie.common.ConsumerException;
import com.itheima.reggie.entity.*;
import com.itheima.reggie.mapper.OrderMapper;
import com.itheima.reggie.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.invoke.LambdaMetafactory;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orders> implements OrderService {
    @Resource
    private AddressBookService addressBookService;
    @Resource
    private UserService userService;
    @Resource
    private ShoppingCartService shoppingCartService;
    @Resource
    private OrderDetailService orderDetailService;

    @Override
    public void submit(Orders orders) {
        //先查询出用户信息
        Long userId = BaseContextUtils.getCurrentId();
        User user = userService.getById(userId);
        //根据userId查询出购物车中的所有信息
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId,userId);
        List<ShoppingCart> shoppingCarts = shoppingCartService.list(lambdaQueryWrapper);
        if(shoppingCarts ==null || shoppingCarts.size()==0){
            throw new ConsumerException("购物车为空，不能下单");
        }
        //查询出地址信息
        Long addressBookId = orders.getAddressBookId();
        AddressBook addressBook = addressBookService.getById(addressBookId);
        if(addressBook==null){
            throw new ConsumerException("请设置地址之后再下单");
        }
        long orderId = IdWorker.getId();//订单号

        AtomicInteger amount = new AtomicInteger(0);

        List<OrderDetail> orderDetails = shoppingCarts.stream().map((item) -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setNumber(item.getNumber());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            orderDetail.setAmount(item.getAmount());
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;
        }).collect(Collectors.toList());


        orders.setId(orderId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);
        orders.setAmount(new BigDecimal(amount.get()));//总金额
        orders.setUserId(userId);
        orders.setNumber(String.valueOf(orderId));
        orders.setUserName(user.getName());
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));
        //向订单表插入数据，一条数据
        this.save(orders);

        //向订单明细表插入数据，多条数据
        orderDetailService.saveBatch(orderDetails);

        //清空购物车数据
        shoppingCartService.remove(lambdaQueryWrapper);


    }

    @Override
    public Page<OrdersDto> userPage(int page, int pageSize) {
        Page<Orders> pageInfo=new Page<>(page,pageSize);
        Page<OrdersDto> dtoPage=new Page<>();
        //先查询出orders的基本数据
        LambdaQueryWrapper<Orders> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Orders::getUserId, BaseContextUtils.getCurrentId());
        lambdaQueryWrapper.orderByDesc(Orders::getOrderTime);
        this.page(pageInfo,lambdaQueryWrapper);
        BeanUtils.copyProperties(pageInfo,dtoPage,"records");
        //通过orderId查询对应的菜品和套餐
        LambdaQueryWrapper<OrderDetail> dtoLambdaQueryWrapper=new LambdaQueryWrapper<>();
        List<Orders> records = pageInfo.getRecords();
        List<OrdersDto> dtoList=records.stream().map((item)->{
            OrdersDto ordersDto=new OrdersDto();
            Long orderId = item.getId();
            dtoLambdaQueryWrapper.eq(OrderDetail::getOrderId,orderId);
            List<OrderDetail> orderDetailList = orderDetailService.list(dtoLambdaQueryWrapper);
            BeanUtils.copyProperties(item,ordersDto);
            ordersDto.setOrderDetails(orderDetailList);
            return ordersDto;
        }).collect(Collectors.toList());
        dtoPage.setRecords(dtoList);
        return dtoPage;

    }

    @Override
    public void again(Orders order) {
        //获取订单号
        Orders orderId = this.getById(order.getId());
        //根据订单号查询出orderDetail信息
        LambdaQueryWrapper<OrderDetail> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(OrderDetail::getOrderId,orderId);
        List<OrderDetail> orderDetails = orderDetailService.list(lambdaQueryWrapper);
        List<ShoppingCart> list = orderDetails.stream().map((item) -> {
            //把从order表中和order_details表中获取到的数据赋值给这个购物车对象
            ShoppingCart shoppingCart = new ShoppingCart();
            shoppingCart.setName(item.getName());
            shoppingCart.setImage(item.getImage());
            shoppingCart.setUserId(BaseContextUtils.getCurrentId());
            shoppingCart.setDishId(item.getDishId());
            shoppingCart.setSetmealId(item.getSetmealId());
            shoppingCart.setDishFlavor(item.getDishFlavor());
            shoppingCart.setNumber(item.getNumber());
            shoppingCart.setAmount(item.getAmount());
            return shoppingCart;
        }).collect(Collectors.toList());
        shoppingCartService.saveBatch(list);
    }
}
