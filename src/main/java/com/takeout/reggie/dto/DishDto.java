package com.takeout.reggie.dto;

import com.takeout.reggie.entity.Dish;
import com.takeout.reggie.entity.DishFlavor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Date Transfer Object, 即数据传输对象
 * 用于封装页面提交的数据
 */
@Data
public class DishDto extends Dish {

    //菜品对应的口味数据
    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;


}
