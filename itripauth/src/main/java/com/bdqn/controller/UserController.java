package com.bdqn.controller;

import cn.itrip.common.*;
import cn.itrip.dao.itripUser.ItripUserMapper;
import cn.itrip.pojo.ItripUser;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bdqn.biz.SentSSM;
import com.bdqn.biz.TokenBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.jaxen.function.StringFunction;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import sun.awt.SunHints;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;
import java.util.Random;

@Controller
@RequestMapping("/api")
@Api(value = "api",description = "用户模块")
public class UserController {

    @Resource
    ItripUserMapper dao;

    @Resource
    TokenBiz tokenBiz;

    @Resource
    JredisApi jredisApi;

    @Resource
    SentSSM sentSSM;

    //手机注册短信验证
    @RequestMapping(value="/validatephone")
    @ResponseBody
    public Dto getValidatePhone(String user,String code)throws Exception{

        try {
            //去redis中查找数据
            String result = jredisApi.getRedis("Code:"+user);
            if(result.equals(code)){
                //根据手机号查询实体，然后update
                dao.updateById(user);
                return DtoUtil.returnSuccess("激活成功 ");
            }
        }catch (Exception e){
            return  DtoUtil.returnFail("激活失败","1000");
        }
        return  DtoUtil.returnFail("激活失败","1000");
    }

    //手机注册
    @RequestMapping(value="/registerbyphone",method = RequestMethod.POST)
    @ResponseBody
    public Dto getRegisterByPhone(@RequestBody ItripUser itripUser) throws Exception{
       try {
           //发送验证码 随机数
           Random random = new Random(7);
           int i = random.nextInt(9999);
           //把四位随机数发送给手机
           sentSSM.setPhone(itripUser.getUserCode(),""+i);
           //存入到redis中
           jredisApi.setRedis("Code:"+itripUser.getUserCode(),""+i,100);
           //把实体类存入到数据库中去
           ItripUser user = new ItripUser();
           user.setUserCode(itripUser.getUserCode());
           user.setUserName(itripUser.getUserName());
           user.setUserPassword(MD5.getMd5(itripUser.getUserPassword(),32));
           user.setActivated(0);

           Integer result = dao.insertItripUser(user);
           if(result>0){
               //成功
               return DtoUtil.returnDataSuccess(user);
           }
       }catch (Exception e){
           return DtoUtil.returnFail("注册失败！","1000");
       }
           return DtoUtil.returnFail("注册失败！","1000");
    }

    //登录
    @RequestMapping(value="/dologin",method = RequestMethod.POST)
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(paramType="form",required=true,value="用户名",name="name"),
            @ApiImplicitParam(paramType="form",required=true,value="密码",name="password")
    })
    public Dto getList (HttpServletRequest request, String name, String password)throws Exception{
        try {
            //判断数据库中是否存在
            ItripUser itripUser= dao.ifLogin(name, MD5.getMd5(password,32));
            //把标识存入到redis中
            if(itripUser!=null){
                //生成一个toKen
                String token=tokenBiz.generateToken(request.getHeader("User-Agent"),itripUser);
                //存入到redis中
                jredisApi.setRedis(token,JSONObject.toJSONString(itripUser),60*60*2);

                //返回前台页面需要当前时间与过期时间
                ItripTokenVO itripTokenVO = new
                        ItripTokenVO(token, Calendar.getInstance().getTimeInMillis()*3600*2,
                        Calendar.getInstance().getTimeInMillis());

                return DtoUtil.returnDataSuccess(itripTokenVO);
            }

        }catch (Exception e){
            return DtoUtil.returnFail("登录失败！","1000");
        }
        return DtoUtil.returnFail("登录失败！","1000");

    }

}
