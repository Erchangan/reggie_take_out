package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.itheima.reggie.Utils.BaseContextUtils;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.AddressBook;
import com.itheima.reggie.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/addressBook")
public class AddressBookController {
    @Resource
    private AddressBookService addressBookService;
    /**
     * 保存地址信息
     */
    @PostMapping
    public R<AddressBook> save(@RequestBody AddressBook addressBook){
        addressBook.setUserId(BaseContextUtils.getCurrentId());
        addressBookService.save(addressBook);
        return R.success(addressBook);
    }

    /**
     * 删除地址
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam Long ids){
        LambdaQueryWrapper<AddressBook> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(AddressBook::getId,ids);
        addressBookService.remove(lambdaQueryWrapper);
        return R.success("删除成功");
    }

    /**
     * 修改地址信息
     * @param addressBook
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody AddressBook addressBook){
        addressBookService.updateById(addressBook);
        return R.success("修改成功");
    }

    /**
     * 设置默认地址
     * @param addressBook
     * @return
     */
    @PutMapping("/default")
    public R<AddressBook> setDefault(@RequestBody AddressBook addressBook){
        //首先user_id查询出该用户所有的地址信息
        LambdaUpdateWrapper<AddressBook> lambdaUpdateWrapper=new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(AddressBook::getUserId,BaseContextUtils.getCurrentId());
        lambdaUpdateWrapper.set(AddressBook::getIsDefault,0);
        addressBookService.update(lambdaUpdateWrapper);
        //再根据id将用户当前的地址信息设置为默认地址
        addressBook.setIsDefault(1);
        addressBookService.updateById(addressBook);
        return R.success(addressBook);
    }

    /**
     * 根据id查询地址
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R getById(@PathVariable Long id){
        AddressBook addressBook = addressBookService.getById(id);
        if(addressBook!=null){
            return R.success(addressBook);
        }
        return R.error("未查询到地址");
    }

    /**
     * 查询默认地址
     * @return
     */
    @GetMapping("/default")
    public R<AddressBook> getDefault(){
        LambdaQueryWrapper<AddressBook> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(AddressBook::getUserId,BaseContextUtils.getCurrentId());
        lambdaQueryWrapper.eq(AddressBook::getIsDefault,1);
        AddressBook addressBook = addressBookService.getOne(lambdaQueryWrapper);
        if(addressBook!=null){
            return R.success(addressBook);
        }else {
            return R.error("未找到地址");
        }
    }
    @GetMapping("/list")
    public R<List<AddressBook>> list(){
        LambdaUpdateWrapper<AddressBook> lambdaUpdateWrapper=new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(AddressBook::getUserId,BaseContextUtils.getCurrentId());
        lambdaUpdateWrapper.orderByDesc(AddressBook::getUpdateTime);
        List<AddressBook> list = addressBookService.list(lambdaUpdateWrapper);
        return R.success(list);
    }
}
