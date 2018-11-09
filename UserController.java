package com.jt.sso.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.druid.util.StringUtils;
import com.jt.common.po.User;
import com.jt.common.vo.SysResult;
import com.jt.sso.service.UserService;

import redis.clients.jedis.JedisCluster;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private JedisCluster jedisCluster;
	
	/**
	 * 完成用户信息校验
	 * url:http://sso.jt.com/user/check/{param}/{type}
	 */
	@RequestMapping("/check/{param}/{type}")
	@ResponseBody
	public MappingJacksonValue findCheckUser(
			@PathVariable String param,
			@PathVariable Integer type,
			String callback){
		//true 表示数据存在  false表示数据不存在
		boolean flag = 
				userService.findcheckUser(param,type);
		SysResult sysResult = SysResult.oK(flag);
		MappingJacksonValue jacksonValue = 
				new MappingJacksonValue(sysResult);
		jacksonValue.setJsonpFunction(callback);
		return jacksonValue;
	}
	
	/**
	 * http://sso.jt.com/user/register
	 * @return
	 */
	@RequestMapping("/register")
	@ResponseBody
	public SysResult saveUser(User user){
		try {
			userService.saveUser(user);
			return SysResult.oK();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return SysResult.build(201,"用户新增失败");
	}
	
	@RequestMapping("/login")
	@ResponseBody
	public SysResult findUserByUP(User user){
		try {
			String token = userService.findUserByUP(user);
			if(!StringUtils.isEmpty(token))
				return SysResult.oK(token);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return SysResult.build(201,"用户名或密码错误");
	}
	
	//根据token数据查询用户信息
	@RequestMapping("/query/{token}")
	@ResponseBody
	public MappingJacksonValue findUserByToken(
			@PathVariable String token,String callback){
		String userJSON = jedisCluster.get(token);
		MappingJacksonValue jacksonValue = null;
		if(StringUtils.isEmpty(userJSON)){
			//如果缓存数据没有则直接201返回即可
			jacksonValue = 
			new MappingJacksonValue(SysResult.build(201,"用户查询失败"));
		}else{
			jacksonValue = 
			new MappingJacksonValue(SysResult.oK(userJSON));
		}
		jacksonValue.setJsonpFunction(callback);
		return jacksonValue;
	}
}

/*@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UserService userService;
	@Autowired
	private JedisCluster jedisCluster;
	//完成用户信息的校验
	@RequestMapping("/check/{param}/{type}")
	@ResponseBody
	public MappingJacksonValue findCheckUser(
						@PathVariable String param,
						@PathVariable Integer type,
						String callback){
		//true 表示数据存在 false表示数据不存在
		boolean flag = userService.findCheckUser(param,type);
		
		SysResult sysResult = SysResult.oK(flag);
		MappingJacksonValue jacksonValue = new MappingJacksonValue(sysResult);
		jacksonValue.setJsonpFunction(callback);
		return jacksonValue;
	}
	@RequestMapping("/register")
	@ResponseBody
	public SysResult saveUser(User user){
		try {
			userService.saveUser(user);
			return SysResult.oK();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return SysResult.build(201, "用户新增失败");
	}
	@RequestMapping("/login")
	@ResponseBody
	public SysResult findUserByUP(User user){
		try {
			String token = userService.findUserByUP(user);
			
			if(!StringUtils.isEmpty(token))
				return SysResult.oK();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return SysResult.build(201, "用户名或密码错误3");
	}
	//根据token数据查询用户信息
	@RequestMapping("/query/{token}")
	@ResponseBody
	public MappingJacksonValue findUserByToken(@PathVariable String token,@PathVariable String callback){
		String userJSON = jedisCluster.get(token);
		MappingJacksonValue jacksonValue = null;
		if(StringUtils.isEmpty(userJSON)){
			jacksonValue = new MappingJacksonValue(SysResult.build(201, "用户名不存在"));
		}else {
			jacksonValue = new MappingJacksonValue(SysResult.oK(userJSON));
		}
		jacksonValue.setJsonpFunction(callback);
		return jacksonValue;
	}
}*/
