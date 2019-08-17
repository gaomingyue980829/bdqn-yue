package cn.itrip.common;


import cn.itrip.pojo.ItripUser;

/**
 * Token管理接口
 * @author hduser
 *
 */
public interface TokenService {


	public String generateToken(String agent, ItripUser user);


	public void save(String token, ItripUser user);


}
