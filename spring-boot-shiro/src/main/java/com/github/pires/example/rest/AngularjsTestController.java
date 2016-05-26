package com.github.pires.example.rest;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AngularjsTestController implements InitializingBean{

	@RequestMapping("/resource")
	public Map<String, Object> home() {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("id", UUID.randomUUID().toString());
		model.put("content", "Hello World from "+SecurityUtils.getSubject().getPrincipal());
		return model;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		System.out.println(".....");
	}
}