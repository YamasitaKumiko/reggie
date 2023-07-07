package com.takeout.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.takeout.reggie.common.R;
import com.takeout.reggie.entity.User;
import com.takeout.reggie.service.UserService;
import com.takeout.reggie.utils.SMSUtils;
import com.takeout.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;


    /**
     * 发送验证码
     * @param user
     * @param session
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
        //获取手机号
        String phone = user.getPhone();
        if(StringUtils.isNotEmpty(phone)){
            //生成随机的验证码
            String code = ValidateCodeUtils.generateValidateCode(6).toString();
            log.info("code={}", code);
            //调用阿里云短信服务api发送短信
            //SMSUtils.sendMessage("瑞吉外卖", "SMS_281830007", phone, code);
            //保存生成的验证码到session
            session.setAttribute(phone, code);
            return R.success("手机验证码发送成功");
        }
        return R.error("手机验证码发送失败");
    }

    /**
     *用户登录
     * @param map
     * @param session
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session){
        //获取手机号
        String phone = map.get("phone").toString();
        //获取验证码
        String code = map.get("code").toString();
        //和session中的验证码比对
        Object codeInSession = session.getAttribute(phone);
        //比对成功则登录成功
        if(codeInSession != null && codeInSession.equals(code)){
            //判断手机号是否在用户表里，如果不在自动完成注册
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone, phone);
            User user = userService.getOne(queryWrapper);
            if(user == null){
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            session.setAttribute("user", user.getId());
            return R.success(user);
        }

        return R.error("验证码不一致，登录失败");
    }
}
