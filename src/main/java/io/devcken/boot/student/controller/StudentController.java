package io.devcken.boot.student.controller;

import io.devcken.boot.student.entity.StudentEntity;
import io.devcken.boot.student.service.StudentService;
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
@RequestMapping("/student")
public class StudentController {
	@Autowired
	private StudentService service;

	@RequestMapping(value = "students")
	@ResponseBody
	public Map<String, Object> findAll() {
		Map<String, Object> map = new HashMap<>();

		map.put("students", service.findAll());

		return map;
	}

	@RequestMapping(value = "/save", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> save(@RequestBody @Valid StudentEntity student, BindingResult bindingResult) {
		Map<String, Object> map = new HashMap<>();

		map.put("student", service.save(student));
		map.put("success", true);

		return map;
	}
}
