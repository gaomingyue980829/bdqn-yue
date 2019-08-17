package com.bdqn.controller;

import cn.itrip.common.Dto;
import cn.itrip.common.DtoUtil;
import cn.itrip.dao.itripAreaDic.ItripAreaDicMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Controller
@RequestMapping("/api")
public class AreadicController {

    @Resource
    ItripAreaDicMapper dao;

    //热门城市
    @RequestMapping("/hotel/queryhotcity/{type}")
    @ResponseBody
    public Dto getList(@PathVariable("type") String type){
        return DtoUtil.returnDataSuccess(dao.ifHot(type));
    }
}
