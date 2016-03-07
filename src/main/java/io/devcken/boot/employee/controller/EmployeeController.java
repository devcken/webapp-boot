package io.devcken.boot.employee.controller;

import io.devcken.boot.employee.entity.EmployeeEntity;
import io.devcken.boot.employee.service.EmployeeService;
import io.devcken.exception.InvalidRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/employee")
public class EmployeeController {
	@Autowired
	private EmployeeService service;

	@RequestMapping(value = "/employees")
	@ResponseBody
	public Map<String, Object> findAll() {
		Map<String, Object> map = new HashMap<>();

		map.put("employees", service.findAll());

		return map;
	}

	@RequestMapping(value = "/save", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> save(@RequestBody @Valid EmployeeEntity employeeEntity, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			throw new InvalidRequestException(bindingResult.getObjectName(), bindingResult);
		}

		Map<String, Object> map = new HashMap<>();

		map.put("employee", service.save(employeeEntity));
		map.put("success", true);

		return map;
	}
}
