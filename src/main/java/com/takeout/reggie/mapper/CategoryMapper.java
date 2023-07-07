package com.takeout.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.takeout.reggie.entity.Category;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}
