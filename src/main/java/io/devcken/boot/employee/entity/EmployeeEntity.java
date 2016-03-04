package io.devcken.boot.employee.entity;

import javax.validation.constraints.NotNull;

public class EmployeeEntity {
	@NotNull
	private Long id;

	@NotNull
	private String name;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
