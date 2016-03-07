package io.devcken.boot.student.service;

import com.google.common.collect.Lists;
import io.devcken.boot.student.entity.StudentEntity;
import io.devcken.boot.student.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentService {
	@Autowired
	@Lazy
	private StudentRepository repository;

	public List<StudentEntity> findAll() {
		return Lists.newArrayList(repository.findAll());
	}

	public StudentEntity save(StudentEntity student) {
		return repository.save(student);
	}
}
