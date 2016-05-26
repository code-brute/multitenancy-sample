package com.github.minly.multitenancy.demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.github.minly.multitenancy.demo.model.Employee;
import com.github.minly.multitenancy.demo.repository.EmployeeRepository;

@Service
public class EmployeeService {
	private final static Logger log = LoggerFactory.getLogger(EmployeeService.class);
	
	@Autowired
	EmployeeRepository employeeRepository;

	@Transactional
	public void save(String... persons) {
		int count = 1;
		for (String person : persons) {
			System.out.println(TransactionSynchronizationManager.getCurrentTransactionName());
			log.info("Booking " + person + " in a seat...");
			employeeRepository.save(new Employee(person+count,person,"N/A","N/A"));
			if(count == 3){
				throw new RuntimeException("......");
			}
			//jdbcTemplate.update("insert into BOOKINGS(FIRST_NAME) values (?)", person);
			count++;
		}
	};
}
