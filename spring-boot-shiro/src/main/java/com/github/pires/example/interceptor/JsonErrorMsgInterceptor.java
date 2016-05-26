package com.github.pires.example.interceptor;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * JSON请求响应错误消息处理
 * 
 * @author Minly Wang
 * @since 2016年5月26日
 *
 */
public class JsonErrorMsgInterceptor implements HandlerInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
		if (modelAndView == null)
			return;

		// 因为MappingJackson2JsonView默认会把BindingResult全部过滤掉。
		// 所以我们要想将错误消息输出，要在这里自己处理好。

		// 判断请求是否是.json、方法上是否有@ResponseBody注解，或者类上面是否有@RestController注解
		// 表示为json请求

		if (!request.getRequestURI().endsWith(".json")) {
			HandlerMethod handlerMethod = (HandlerMethod) handler;
			if (handlerMethod.getMethodAnnotation(ResponseBody.class) == null) {
				if (handlerMethod.getBeanType().getAnnotation(RestController.class) == null) {
					return;
				}
			}
		}
		Map<String, Object> modelMap = modelAndView.getModel();
		if (modelMap != null) {
			Map<String, String> errorMsg = null;
			if (modelMap.containsKey("errorMsg")) {
				errorMsg = (Map<String, String>) modelMap.get("errorMsg");
			}
			if (errorMsg == null) {
				errorMsg = new HashMap<>();
				modelMap.put("errorMsg", errorMsg);
			}
			for (Entry<String, Object> entry : modelMap.entrySet()) {
				if (entry.getValue() instanceof BindingResult) {
					BindingResult bindingResult = (BindingResult) entry.getValue();
					if (bindingResult.hasErrors()) {
						for (FieldError fieldError : bindingResult.getFieldErrors()) {
							errorMsg.put(fieldError.getObjectName() + "." + fieldError.getField(), fieldError.getDefaultMessage());
						}
					}
				}
			}
		}
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
	}

}
