package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.Dto.SetmealDto;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;

import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    @Resource
    private SetmealService setmealService;
    @Resource
    private SetmealDishService setmealDishService;
    @Resource
    private CategoryService categoryService;

    /**
     * 新建套餐
     *
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto) {
        setmealService.saveWithDish(setmealDto);
        return R.success("新建套餐成功");
    }

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        Page<Setmeal> pageInfo = new Page<>(page, pageSize);
        Page<SetmealDto> setmealDtoPage = new Page<>();
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(name != null, Setmeal::getName, name);
        lambdaQueryWrapper.orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(pageInfo, lambdaQueryWrapper);
        BeanUtils.copyProperties(pageInfo, setmealDtoPage, "records");
        List<Setmeal> records = pageInfo.getRecords();
        List<SetmealDto> list = records.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }
            return setmealDto;
        }).collect(Collectors.toList());
        setmealDtoPage.setRecords(list);
        return R.success(setmealDtoPage);
    }

    /**
     * 将要修改的数据回显到表单
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<SetmealDto> getByIdWithDish(@PathVariable("id") Long id) {
        SetmealDto setmealDto = setmealService.getByIdWithDish(id);
        return R.success(setmealDto);
    }

    /**
     * 修改套餐信息
     *
     * @param setmealDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto) {
        setmealService.updateWithDish(setmealDto);
        return R.success("修改套餐信息成功");
    }

    /**
     * 批量修改出售状态
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> updateMutStatus(@PathVariable Integer status,Long[] ids) {
        List<Long> list = Arrays.asList(ids);
        LambdaUpdateWrapper<Setmeal> lambdaUpdateWrapper=new LambdaUpdateWrapper();
        lambdaUpdateWrapper.set(Setmeal::getStatus,status).in(Setmeal::getId,list);
        setmealService.update(lambdaUpdateWrapper);
        return R.success("出售状态修改成功");
    }

    /**
     * 批量删除
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){
        //先删除setmeal的基本信息
        setmealService.deleteByIdWithDish(ids);
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(SetmealDish::getSetmealId,ids);
        setmealDishService.remove(lambdaQueryWrapper);
        return R.success("删除成功");
    }

    /**
     * 根据条件查询套餐数据
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(setmeal.getCategoryId()!=null,Setmeal::getCategoryId,setmeal.getCategoryId());
        lambdaQueryWrapper.eq(setmeal.getStatus()!=null,Setmeal::getStatus,setmeal.getStatus());
        lambdaQueryWrapper.orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> list = setmealService.list(lambdaQueryWrapper);
        return R.success(list);
    }
}
