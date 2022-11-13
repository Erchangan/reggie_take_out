package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.service.CategoryService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {
    @Resource
    private CategoryService categoryService;

    /**
     * 新增分类
     * @param category
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Category category){
        categoryService.save(category);
        return R.success("新增分类成功");
    }

    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize){
        Page<Category> pageInfo =new Page(page,pageSize);
        LambdaQueryWrapper<Category> queryWrapper=new LambdaQueryWrapper();
        queryWrapper.orderByAsc(Category::getSort);
        categoryService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }

    /**
     * 
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> deleteById(@RequestParam("ids") Long ids){
        categoryService.remove(ids);
        return R.success("删除成功");
    }

    /**
     * 修改信息
     * @param category
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Category category){
        categoryService.updateById(category);
        return R.success("修改信息成功");
    }

    /**
     * 下拉选项框显示菜品分类
     * @param category
     * @return
     */
    @GetMapping("/list")
    public R<List<Category>> list(Category category){
        LambdaQueryWrapper<Category> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(category.getType()!=null,Category::getType,category.getType());
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        List<Category> list = categoryService.list(queryWrapper);
        return R.success(list);
    }
}
