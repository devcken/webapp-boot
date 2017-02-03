package io.devcken.boot.employee.controller;

import io.devcken.boot.employee.entity.Employee;
import io.devcken.boot.employee.service.EmployeeService;
import io.devcken.exception.InvalidRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/employee")
public class EmployeeController {
	private final EmployeeService service;

	@Inject
	public EmployeeController(EmployeeService service) {
		this.service = service;
	}

	@RequestMapping(value = "/employees")
	@ResponseBody
	public ResponseEntity<List<Employee>> findAll() {
		return ResponseEntity.ok().body(this.service.findAll());
	}

	@RequestMapping(value = "/save", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<Employee> save(@RequestBody @Valid Employee employee, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			throw new InvalidRequestException(bindingResult.getObjectName(), bindingResult);
		}

		employee = service.save(employee);

		return (employee == null ?
				ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR) :
				ResponseEntity.ok()).body(employee);
	}
}
