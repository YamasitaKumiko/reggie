package com.takeout.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.takeout.reggie.entity.OrderDetail;
import com.takeout.reggie.mapper.OrderDetailMapper;
import com.takeout.reggie.service.OrderDetailService;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {

}