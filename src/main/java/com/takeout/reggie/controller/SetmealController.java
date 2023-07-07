package com.takeout.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.takeout.reggie.common.R;
import com.takeout.reggie.dto.SetmealDto;
import com.takeout.reggie.entity.Category;
import com.takeout.reggie.entity.Setmeal;
import com.takeout.reggie.entity.SetmealDish;
import com.takeout.reggie.service.SetmealDishService;
import com.takeout.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 新增套餐
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){
        setmealService.saveWithDish(setmealDto);
        return R.success("新增套餐成功");
    }

    /**
     * 套餐分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        Page<SetmealDto> setmealDtoPage = setmealService.pageWithCategory(page, pageSize, name);
        return R.success(setmealDtoPage);
    }


    /**
     * 删除套餐，同时删除关联的菜品
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){
        setmealService.removeWithDish(ids);
        return R.success("套餐删除成功");
    }

    /**
     * 修改套餐状态
     * @param value
     * @param ids
     * @return
     */
    @PostMapping("/status/{value}")
    public R<String> status(@PathVariable int value, @RequestParam List<Long> ids){
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId, ids);
        List<Setmeal> list = setmealService.list(queryWrapper);
        if (list != null){
            list = list.stream().map((item) -> {
                item.setStatus(value);
                return item;
            }).collect(Collectors.toList());
        }
        setmealService.updateBatchById(list);
        return R.success("套餐状态修改成功");
    }

    /**
     * 根据categoryId返回套餐列表
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal){

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId() != null, Setmeal::getCategoryId, setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus() != null, Setmeal::getStatus, setmeal.getStatus());
        //添加排序条件
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> list = setmealService.list(queryWrapper);
        return R.success(list);
    }

    /**
     * 根据setmealID返回对应菜品列表
     * @param id
     * @return
     */
    @GetMapping("/dish/{id}")
    public R<List<SetmealDish>> dishList(@PathVariable Long id){
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId, id);
        queryWrapper.orderByAsc(SetmealDish::getSort);
        List<SetmealDish> list = setmealDishService.list(queryWrapper);
        return R.success(list);
    }
}
