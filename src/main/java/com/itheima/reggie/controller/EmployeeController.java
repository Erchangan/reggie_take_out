package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@RestController
@Slf4j
@RequestMapping("/employee")
public class EmployeeController {
    @Resource
    private EmployeeService employeeService;

    /**
     * 登录方法
     *
     * @param employee
     * @param request
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(@RequestBody Employee employee, HttpServletRequest request) {
        //将用户提交的密码进行加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        //根据页面提交的用户名查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);
        //用户名比对
        if (emp == null) {
            return R.error("登陆失败");
        }
        //密码比对
        if (!emp.getPassword().equals(password)) {
            return R.error("登陆失败");
        }
        //用户状态比对
        if (emp.getStatus() == 0) {
            return R.error("账户已禁用");
        }
        //登陆成功，将员工id存入session
        request.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);
    }

    /**
     * 退出方法
     *
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    /**
     * 新增员工
     *
     * @param employee
     * @param request
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Employee employee, HttpServletRequest request) {
        //新增员工信息
        log.info("新增员工信息{}", employee.toString());
        //设置初始密码,通过MD5加密
        String password = DigestUtils.md5DigestAsHex("123456".getBytes());
        employee.setPassword(password);
        //使用MybatisPlus提供的字段自动填充功能代替
        //employee.setCreateTime(LocalDateTime.now());
        //employee.setUpdateTime(LocalDateTime.now());
        //Long empId = (Long) request.getSession().getAttribute("employee");
        // employee.setCreateUser(empId);
        //employee.setUpdateUser(empId);
        employeeService.save(employee);
        return R.success("新增员工成功");
    }

    /**
     * 分页查询
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        //构造分页构造器
        Page pageInfo = new Page(page, pageSize);
        //构造条案件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.like(StringUtils.isEmpty(name), Employee::getName, name);
        //添加排序条件
        queryWrapper.orderByDesc(Employee::getCreateTime);
        employeeService.page(pageInfo);
        return R.success(pageInfo);
    }

    @PutMapping
    public R<String> update(@RequestBody Employee employee, HttpServletRequest request) {
        long id = Thread.currentThread().getId();
        log.info("当前线程id{}", id);
//        Long empId = (Long) request.getSession().getAttribute("employee");
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setUpdateUser(empId);
        employeeService.updateById(employee);
        return R.success("员工信息修改成功");
    }

    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable("id") Long id) {
        Employee employee = employeeService.getById(id);
        if (employee != null) {
            return R.success(employee);
        }
        return R.error("员工不存在");
    }
}
