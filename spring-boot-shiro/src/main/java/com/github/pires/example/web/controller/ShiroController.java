package com.github.pires.example.web.controller;

import java.time.LocalDateTime;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.zama.examples.multitenant.util.Constants;

import com.github.pires.example.authentication.TenantAuthenticationToken;
import com.github.pires.example.entity.User;

/**
 * 
 * @author Minly Wang
 * @since 2016年5月26日
 *
 */
@Controller
@Transactional(value = "tenantTransactionManager")
public class ShiroController {

	private static final Logger logger = LoggerFactory.getLogger(ShiroController.class);

	@RequestMapping(value = "/")
	public String index() {
		return "index";
	}

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String loginForm(Model model) {
		model.addAttribute("user", new User());
		return "login";
	}

	@RequestMapping(value = "/home", method = RequestMethod.GET)
	public String home() {
		return "home";
	}

	@RequestMapping(value = "/angularjs")
	public String angularjs() {
		return "angularjs";
	}

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String login(@Valid User user, HttpSession session, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			return "login";
		}
		session.setAttribute("test", "测试");

		String username = user.getUsername();
		UsernamePasswordToken token = new TenantAuthenticationToken(user.getUsername(), user.getPassword(), user.getTenantId());
		token.setRememberMe(user.isRememberMe());
		// 获取当前的Subject
		Subject currentUser = SecurityUtils.getSubject();
		logger.info("从HttpSession中读取Attr : test = {}", currentUser.getSession().getAttribute("test"));
		try {
			// 在调用了login方法后,SecurityManager会收到AuthenticationToken,并将其发送给已配置的Realm执行必须的认证检查
			// 每个Realm都能在必要时对提交的AuthenticationTokens作出反应
			// 所以这一步在调用login(token)方法时,它会走到MyRealm.doGetAuthenticationInfo()方法中,具体验证方式详见此方法
			logger.info("对用户[" + username + "]进行登录验证..验证开始");
			SecurityUtils.getSubject().getSession().setAttribute(Constants.CURRENT_TENANT_IDENTIFIER, user.getTenantId());
			currentUser.login(token);
			logger.info("对用户[" + username + "]进行登录验证..验证通过");
		} catch (UnknownAccountException uae) {
			logger.info("对用户[" + username + "]进行登录验证..验证未通过,未知账户");
			redirectAttributes.addFlashAttribute("message", "未知账户");
		} catch (IncorrectCredentialsException ice) {
			logger.info("对用户[" + username + "]进行登录验证..验证未通过,错误的凭证");
			redirectAttributes.addFlashAttribute("message", "密码不正确");
		} catch (LockedAccountException lae) {
			logger.info("对用户[" + username + "]进行登录验证..验证未通过,账户已锁定");
			redirectAttributes.addFlashAttribute("message", "账户已锁定");
		} catch (ExcessiveAttemptsException eae) {
			logger.info("对用户[" + username + "]进行登录验证..验证未通过,错误次数过多");
			redirectAttributes.addFlashAttribute("message", "用户名或密码错误次数过多");
		} catch (AuthenticationException ae) {
			// 通过处理Shiro的运行时AuthenticationException就可以控制用户登录失败或密码错误时的情景
			logger.info("对用户[" + username + "]进行登录验证..验证未通过,堆栈轨迹如下");
			ae.printStackTrace();
			redirectAttributes.addFlashAttribute("message", "用户名或密码不正确");
		}
		// 验证是否登录成功
		if (currentUser.isAuthenticated()) {
			logger.info("用户[" + username + "]登录认证通过(这里可以进行一些认证通过后的一些系统参数初始化操作)");
			return "redirect:/home";
		} else {
			token.clear();
			return "redirect:/login";
		}
	}

	@RequestMapping("/403")
	public String unauthorizedRole() {
		logger.info("------没有权限-------");
		return "403";
	}

	@RequestMapping("/404")
	public String error404() {
		int hour = LocalDateTime.now().getHour();
		if(hour<8 || hour>18){
			return "404/night/404";
		}
		return "404/day/404";
	}
	@RequestMapping("/500")
	public String error500() {
		return "500";
	}

	@RequestMapping("/unauthorized")
	public String unauthorized() {
		logger.info("------没有权限-------");
		return "unauthorized";
	}

}
