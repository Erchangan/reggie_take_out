package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.Utils.ValidateCodeUtils;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.User;
import com.itheima.reggie.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Resource
    private UserService userService;

    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session) {
        String phone = user.getPhone();
        if (StringUtils.isNotEmpty(phone)) {
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("生成验证码{}", code);
            session.setAttribute(phone, code);
            return R.success("验证码发送成功");
        }
        return R.success("验证码发送失败");
    }

    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session) {
        log.info(map.toString());
        String phone = map.get("phone").toString();
        String code = map.get("code").toString();
        Object sessionPhone = session.getAttribute(phone);
        if (sessionPhone != null && sessionPhone.equals(code)) {
            LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(User::getPhone, phone);
            User user = userService.getOne(lambdaQueryWrapper);
            //判断是否为新用户
            if (user == null) {
                user =new User();
                user.setPhone(phone);
                userService.save(user);

            }
            session.setAttribute("user",user.getId());
            return R.success(user);

        }
        return R.error("登陆失败");
    }

    /**
     * 退出登录
     * @param session
     * @return
     */
    @PostMapping("/loginout")
    public R<String> loginOut(HttpSession session){
        session.removeAttribute("user");
        return R.success("退出成功");
    }

}
