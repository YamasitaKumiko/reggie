package com.takeout.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.takeout.reggie.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

public interface CategoryService extends IService<Category> {
    public void remove(Long ids);
}
