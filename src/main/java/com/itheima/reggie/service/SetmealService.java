package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.Dto.SetmealDto;
import com.itheima.reggie.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    void saveWithDish(SetmealDto setmealDto);
    SetmealDto getByIdWithDish(Long id);
    void updateWithDish(SetmealDto setmealDto);
    void deleteByIdWithDish(List<Long> ids);
}
