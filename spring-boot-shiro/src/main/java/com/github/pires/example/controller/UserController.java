package com.github.pires.example.controller;

import java.time.LocalDateTime;
import java.util.Optional;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.credential.DefaultPasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.zama.examples.multitenant.annotation.TenantTransactional;
import org.zama.examples.multitenant.util.MultiTenantUtils;

import com.github.pires.example.model.Role;
import com.github.pires.example.model.User;
import com.github.pires.example.repository.RoleRepository;
import com.github.pires.example.repository.UserRepository;

@Controller
@TenantTransactional
@RequestMapping("/users")
public class UserController {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private DefaultPasswordService passwordService;

	@RequestMapping
	public String users(Model model) {
		model.addAttribute("user", new User());
		model.addAttribute("welcome", "Welcome " + SecurityUtils.getSubject().getPrincipal() + " from " + MultiTenantUtils.getCurrentTenantId());
		model.addAttribute("users", userRepository.findAll());
		return "users";
	}

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public String addUser(@ModelAttribute User user, Model model) {
		user.setActive(true);
		user.setPassword(passwordService.encryptPassword(user.getPassword()));
		user.setCreated(LocalDateTime.now());
		user.setCreatedBy((String) SecurityUtils.getSubject().getPrincipal());
		Optional<Role> role = roleRepository.findOneByName("USER");
		if (role.isPresent()) {
			user.getRoles().add(role.get());
		}
		userRepository.save(user);
		return "redirect:/users";
	}
}
