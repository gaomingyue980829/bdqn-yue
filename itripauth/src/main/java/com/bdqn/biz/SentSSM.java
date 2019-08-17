package com.bdqn.biz;

import com.cloopen.rest.sdk.CCPRestSmsSDK;
import com.fasterxml.jackson.core.SerializableString;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class SentSSM {
    public boolean setPhone(String phone,String code){
        HashMap<String,Object> result = null;

        CCPRestSmsSDK restAPI = new CCPRestSmsSDK();

        restAPI.init("app.cloopen.com","8883");
        restAPI.setAccount("8a216da869dca619016a0f86d86316b7","6de9551641a54a67bac995e7a2995809");
        restAPI.setAppId("8a216da869dca619016a0f86d8ae16bd");
        result = restAPI.sendTemplateSMS(phone,"1" ,new String[]{code,"1"});
        //result = restAPI.sendTemplateSMS(phone,"1",new String[]{});

        System.out.println("SDKTestGAETsUBaCCOUNTS result="+result);
        if("000000".equals(result.get("statusCode"))){

            return true;
        }else {
            //异常返回输出错误吗和错误信息
            System.out.println("错误码=" + result.get("statusCode") +" 错误信息= "+result.get("statusMsg"));
        }
        return false;
    }

    //测试
    public static void main(String[] args) {
        SentSSM sentSSM = new SentSSM();
        sentSSM.setPhone("15652073888","1234");
    }

    }
