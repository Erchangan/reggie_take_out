package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.Dto.SetmealDto;
import com.itheima.reggie.common.ConsumerException;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.mapper.SetmealMapper;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    @Resource
    private SetmealDishService setmealDishService;
    @Override
    public void saveWithDish(SetmealDto setmealDto) {
        //保存套餐的基本信息
        this.save(setmealDto);
        //保存套餐中的菜品信息，并set菜品所属的套餐也就是记录setmealId
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((item)->{
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        setmealDishService.saveBatch(setmealDishes);

    }

    @Override
    @Transactional
    public SetmealDto getByIdWithDish(Long id) {
        //首先查询到基础信息
        Setmeal setmeal = this.getById(id);
        SetmealDto setmealDto=new SetmealDto();
        BeanUtils.copyProperties(setmeal,setmealDto);
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper=new LambdaQueryWrapper();
        lambdaQueryWrapper.eq(SetmealDish::getSetmealId, id);
        List<SetmealDish> list = setmealDishService.list(lambdaQueryWrapper);
        setmealDto.setSetmealDishes(list);
        return setmealDto;
    }

    @Override
    public void updateWithDish(SetmealDto setmealDto) {
        //更新基本信息
        this.updateById(setmealDto);
        //清除原有的对应套餐的菜品
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SetmealDish::getSetmealId,setmealDto.getId());
        setmealDishService.remove(lambdaQueryWrapper);
        //保存新的菜品
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((item)->{
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
            setmealDishService.saveBatch(setmealDishes);
    }

    @Override
    public void deleteByIdWithDish(List<Long> ids) {
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(ids != null, Setmeal::getId,ids);
        List<Setmeal> list = this.list(lambdaQueryWrapper);
        for(Setmeal setmeal:list){
            Integer status = setmeal.getStatus();
            if(status == 0){
                this.removeById(setmeal.getId());
            }else {
                throw new ConsumerException("套餐正在售卖，无法删除");
            }
        }

    }
}
