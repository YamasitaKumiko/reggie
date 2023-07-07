package com.takeout.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.takeout.reggie.common.CustomException;
import com.takeout.reggie.dto.SetmealDto;
import com.takeout.reggie.entity.Setmeal;
import com.takeout.reggie.entity.SetmealDish;
import com.takeout.reggie.mapper.SetmealMapper;
import com.takeout.reggie.service.CategoryService;
import com.takeout.reggie.service.SetmealDishService;
import com.takeout.reggie.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService{

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增套餐，同时保存套餐和菜品的关联关系
     * @param setmealDto
     */
    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        //保存套餐基本信息
        this.save(setmealDto);
        //保存关联菜品信息,插入setmeal_dish表
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes = setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * 分页查询套餐，同时返回套餐分类名称
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @Override
    @Transactional
    public Page<SetmealDto> pageWithCategory(int page, int pageSize, String name) {
        Page<Setmeal> pageInfo = new Page<>(page, pageSize);
        Page<SetmealDto> dtoPage = new Page<>();
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name != null, Setmeal::getName, name);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        this.page(pageInfo, queryWrapper);
        BeanUtils.copyProperties(pageInfo, dtoPage, "records");
        List<Setmeal> records = pageInfo.getRecords();
        List<SetmealDto> list = records.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);
            String categoryName = categoryService.getById(item.getCategoryId()).getName();
            setmealDto.setCategoryName(categoryName);
            return setmealDto;
        }).collect(Collectors.toList());
        dtoPage.setRecords(list);
        return dtoPage;
    }

    /**
     * 删除套餐，同时删除关联的菜品
     * @param ids
     */
    @Override
    @Transactional
    public void removeWithDish(List<Long> ids) {
        //查询套餐状态，停售时才可以删除
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId, ids).eq(Setmeal::getStatus, 1);
        int count = this.count(queryWrapper);
        //如果不能删除，抛出业务异常
        if (count > 0){
            throw new CustomException("套餐正在售卖中，请停售后再删除");
        }
        //如果可以删除，先删除套餐表里的数据
        this.removeByIds(ids);
        //删除关系表setmeal_dish里的数据
        LambdaQueryWrapper<SetmealDish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.in(SetmealDish::getSetmealId, ids);
        setmealDishService.remove(dishLambdaQueryWrapper);
    }

}
