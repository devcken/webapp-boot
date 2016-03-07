package io.devcken.boot.employee.service;

import io.devcken.boot.employee.entity.EmployeeEntity;
import io.devcken.boot.employee.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {
	@Autowired
	private EmployeeRepository repository;

	public List findAll() {
		return repository.findAll();
	}

	public EmployeeEntity save(EmployeeEntity employeeEntity) {
		return repository.save(employeeEntity);
	}
}
