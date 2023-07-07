package com.takeout.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.takeout.reggie.common.BaseContext;
import com.takeout.reggie.common.R;
import com.takeout.reggie.entity.ShoppingCart;
import com.takeout.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 添加购物车
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){
        //设置用户id，指定当前是哪个用户的购物车数据
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);
        //查询当前菜品或套餐是否在购物车中
        Long dishId = shoppingCart.getDishId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, userId);
        if(dishId != null){
            //菜品
            queryWrapper.eq(ShoppingCart::getDishId, dishId);
        }else {
            //套餐
            queryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }
        ShoppingCart cart = shoppingCartService.getOne(queryWrapper);
        if(cart != null){
            //已存在就在原来的数量基础上+1
            Integer number = cart.getNumber();
            cart.setNumber(number + 1);
            shoppingCartService.updateById(cart);
        }else {
            //不存在则添加到购物车，数量默认为1
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            cart = shoppingCart;
        }

        return R.success(cart);
    }

    /**
     * 减少购物车商品数量
     * @param shoppingCart
     * @return
     */
    @PostMapping("/sub")
    public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart){
        //设置用户id，指定当前是哪个用户的购物车数据
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);
        //查询当前菜品或套餐数量
        Long dishId = shoppingCart.getDishId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, userId);
        if(dishId != null){
            //菜品
            queryWrapper.eq(ShoppingCart::getDishId, dishId);
        }else {
            //套餐
            queryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }
        ShoppingCart cartServiceOne = shoppingCartService.getOne(queryWrapper);
        log.info(cartServiceOne.toString());
        Integer number = cartServiceOne.getNumber();
        if(number > 1){
            //大于1数量-1
            cartServiceOne.setNumber(number - 1);
            shoppingCartService.updateById(cartServiceOne);
        }else {
            //等于1清空这条数据
            cartServiceOne.setNumber(0);
            shoppingCartService.remove(queryWrapper);
        }
        return R.success(cartServiceOne);

    }

    /**
     * 根据登录id查看购物车
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        queryWrapper.orderByAsc(ShoppingCart::getCreateTime);
        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);
        return R.success(list);
    }

    /**
     * 清空购物车
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> clean(){
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        shoppingCartService.remove(queryWrapper);
        return R.success("购物车清空成功");
    }
}
