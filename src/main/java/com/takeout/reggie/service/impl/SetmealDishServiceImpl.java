package com.takeout.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.takeout.reggie.entity.SetmealDish;
import com.takeout.reggie.mapper.SetmealDishMapper;
import com.takeout.reggie.service.SetmealDishService;
import org.springframework.stereotype.Service;

@Service
public class SetmealDishServiceImpl extends ServiceImpl<SetmealDishMapper, SetmealDish> implements SetmealDishService {
}
