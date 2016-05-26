package com.github.minly.multitenancy.demo.web;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.github.minly.multitenancy.demo.model.Employee;
import com.github.minly.multitenancy.demo.repository.EmployeeRepository;
import com.github.minly.multitenancy.identifier.TenantIdentifierStorage;

@Controller
@RequestMapping("/{tenantid}")
public class EmployeeController {

	@Autowired
	private EmployeeRepository employeeRepository;
	
	@Autowired
	TenantIdentifierStorage tenantIdentifierStorage;

	@RequestMapping
	public String employees(@PathVariable String tenantid, Model model) {
		TenantIdentifierStorage.setThreadTenantId(tenantid);
		model.addAttribute("tenantid", tenantid);
		model.addAttribute("employee", new Employee());
		model.addAttribute("employees", employeeRepository.findAll());
		return "employees";
	}

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@Transactional
	public String addEmployee(@ModelAttribute Employee employee, Model model) {
		employeeRepository.save(employee);
		return "redirect:/{tenantid}";
	}
}
