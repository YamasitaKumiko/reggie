package com.takeout.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.takeout.reggie.dto.DishDto;
import com.takeout.reggie.entity.Dish;
import com.takeout.reggie.entity.DishFlavor;
import com.takeout.reggie.entity.Setmeal;
import com.takeout.reggie.mapper.DishMapper;
import com.takeout.reggie.mapper.SetmealMapper;
import com.takeout.reggie.service.DishFlavorService;
import com.takeout.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService{

    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * 新增菜品，同时插入菜品对应的口味数据
     * @param dishDto
     */
    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        //保存菜品基本信息到菜品表dish
        this.save(dishDto);
        //获取菜品id：dishId
        Long dishId = dishDto.getId();
        //保存菜品口味到菜品口味表dish_flavor
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 根据id查询菜品及对应的口味信息
     * @param id
     * @return
     */
    @Override
    @Transactional
    public DishDto getByIdWithFlavor(Long id) {
        //查询菜品基本信息
        Dish dish = this.getById(id);
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish, dishDto);
        //查询对应的口味信息
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, id);
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(flavors);
        return dishDto;
    }

    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        //更新菜品表
        this.updateById(dishDto);
        //请理菜品对应的口味信息
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dishDto.getId());
        dishFlavorService.remove(queryWrapper);
        //插入菜品对应的口味信息
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);
    }
}
