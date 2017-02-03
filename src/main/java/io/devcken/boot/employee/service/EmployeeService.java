package io.devcken.boot.employee.service;

import io.devcken.boot.employee.entity.Employee;
import io.devcken.boot.employee.repository.EmployeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;

@Service
public class EmployeeService {
	private final EmployeeRepository repository;

	@Inject
	public EmployeeService(EmployeeRepository repository) {
		this.repository = repository;
	}

	public List<Employee> findAll() {
		return repository.findAll();
	}

	@Transactional
	public Employee save(Employee employee) {
		return repository.save(employee);
	}
}
