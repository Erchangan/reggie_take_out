package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.ConsumerException;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.mapper.CategoryMapper;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishService;
import com.itheima.reggie.service.SetmealService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Resource
    private DishService dishService;
    @Resource
    private SetmealService setmealService;
    @Override
    public void remove(Long ids) {
        //创建构造条件器
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper =new LambdaQueryWrapper();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,ids);
        int count = dishService.count(dishLambdaQueryWrapper);
        if(count>0){
            throw new ConsumerException("当前分类下关联了菜品，不能删除");
        }
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper =new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,ids);
        int count1 = setmealService.count(setmealLambdaQueryWrapper);
        if(count1>0){
            //已关联套餐，抛出异常
            throw new ConsumerException("当前套餐下关联了菜品，不能删除");
        }
        super.removeById(ids);
    }
}
