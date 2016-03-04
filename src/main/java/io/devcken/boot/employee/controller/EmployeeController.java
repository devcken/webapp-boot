package io.devcken.boot.employee.controller;

import io.devcken.boot.employee.entity.EmployeeEntity;
import io.devcken.exception.InvalidRequestException;
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
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> save(@RequestBody @Valid EmployeeEntity employeeEntity, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			throw new InvalidRequestException(bindingResult.getObjectName(), bindingResult);
		}

		Map<String, Object> map = new HashMap<>();

		map.put("success", true);
		map.put("employee", employeeEntity);

		return map;
	}
}
