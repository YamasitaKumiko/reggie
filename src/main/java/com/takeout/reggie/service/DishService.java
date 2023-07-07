package com.takeout.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.takeout.reggie.dto.DishDto;
import com.takeout.reggie.entity.Dish;

public interface DishService extends IService<Dish> {
    //新增菜品，同时插入菜品对应的口味数据
    //同时操作两张表：dish、dish_flavor
    public void saveWithFlavor(DishDto dishDto);

    //更新菜品，同时更新菜品对应的口味数据
    public void updateWithFlavor(DishDto dishDto);

    //根据id查询菜品及对应的口味信息
    public DishDto getByIdWithFlavor(Long id);
}
