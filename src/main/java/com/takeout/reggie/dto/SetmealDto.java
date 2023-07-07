package com.takeout.reggie.dto;

import com.takeout.reggie.entity.Setmeal;
import com.takeout.reggie.entity.SetmealDish;
import lombok.Data;

import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
