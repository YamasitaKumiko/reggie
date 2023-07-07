package com.takeout.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.takeout.reggie.common.CustomException;
import com.takeout.reggie.entity.Category;
import com.takeout.reggie.entity.Dish;
import com.takeout.reggie.entity.Setmeal;
import com.takeout.reggie.mapper.CategoryMapper;
import com.takeout.reggie.service.CategoryService;
import com.takeout.reggie.service.DishService;
import com.takeout.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    /**
     * 根据id删除分类， 删除之前需要进行判断分类里有无菜品
     * @param ids
     */
    @Override
    public void remove(Long ids) {
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId, ids);
        int count1 = dishService.count(dishLambdaQueryWrapper);
        //查询分类是否关联了菜品，关联则抛出业务异常
        if(count1 > 0){
            throw new CustomException("当前分类下关联了菜品，不能删除");
        }
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId, ids);
        int count2 = setmealService.count(setmealLambdaQueryWrapper);
        //查询分类是否关联了套餐，关联则抛出业务异常
        if (count2 > 0){
            throw new CustomException("当前分类下关联了套餐，不能删除");
        }
        //正常删除分类
        super.removeById(ids);
    }
}
