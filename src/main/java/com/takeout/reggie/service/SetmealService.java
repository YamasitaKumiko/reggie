package com.takeout.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.takeout.reggie.dto.SetmealDto;
import com.takeout.reggie.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    /**
     * 新增套餐，同时保存套餐和菜品的关联关系
     * @param setmealDto
     */
    public void saveWithDish(SetmealDto setmealDto);

    /**
     * 分页查询套餐，同时返回套餐分类名称
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    public Page<SetmealDto> pageWithCategory(int page, int pageSize, String name);

    /**
     * 删除套餐，同时删除关联的菜品
     * @param ids
     */
    public void removeWithDish(List<Long> ids);

}
