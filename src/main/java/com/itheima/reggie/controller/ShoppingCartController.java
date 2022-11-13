package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.Utils.BaseContextUtils;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.ShoppingCart;
import com.itheima.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/shoppingCart")
@Slf4j
public class ShoppingCartController {
    @Resource
    private ShoppingCartService shoppingCartService;

    /**
     * 加入购物车
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart) {
        shoppingCart.setUserId(BaseContextUtils.getCurrentId());
        Long dishId = shoppingCart.getDishId();
        //首先查询购物车中是否已经存在Dish或者Setmeal
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId,shoppingCart.getUserId());
        //判断是添加的是Dish还是Setmeal动态封装SQL
        if(dishId!=null){
            lambdaQueryWrapper.eq(ShoppingCart::getDishId,dishId);
        }else{
            lambdaQueryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }
        ShoppingCart cartServiceOne = shoppingCartService.getOne(lambdaQueryWrapper);
        if(cartServiceOne!=null){
            //存在的条件下
            Integer number = cartServiceOne.getNumber();
            cartServiceOne.setNumber(number+1);
            cartServiceOne.setCreateTime(LocalDateTime.now());
            shoppingCartService.updateById(cartServiceOne);
        }else{
            //不存在的条件下
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            cartServiceOne=shoppingCart;
        }
        return R.success(cartServiceOne);
    }

    /**
     * 购物车中菜品数量-1
     * @param shoppingCart
     * @return
     */
    @PostMapping("/sub")
    public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart){
        shoppingCart.setUserId(BaseContextUtils.getCurrentId());
        Long dishId = shoppingCart.getDishId();
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId,shoppingCart.getUserId());
        if(dishId!=null){
            lambdaQueryWrapper.eq(ShoppingCart::getDishId,dishId);
        }else {
            lambdaQueryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }
        ShoppingCart cartServiceOne = shoppingCartService.getOne(lambdaQueryWrapper);
        Integer number = cartServiceOne.getNumber();
        if(number>0){
            cartServiceOne.setNumber(number-1);
        }
        shoppingCartService.updateById(cartServiceOne);
        return R.success(cartServiceOne);
    }

    /**
     * 显示购物车中的信息
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        Long userId = BaseContextUtils.getCurrentId();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId,userId);
        lambdaQueryWrapper.orderByDesc(ShoppingCart::getCreateTime);
        List<ShoppingCart> list = shoppingCartService.list(lambdaQueryWrapper);
        return R.success(list);
    }

    /**
     * 清空购物车
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> clean(){
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId,BaseContextUtils.getCurrentId());
        shoppingCartService.remove(lambdaQueryWrapper);
        return R.success("清空购物车成功");
    }
}
