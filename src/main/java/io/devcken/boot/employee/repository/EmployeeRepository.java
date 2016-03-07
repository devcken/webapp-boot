package io.devcken.boot.employee.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.sql.SQLQueryFactory;
import io.devcken.boot.employee.entity.EmployeeEntity;
import io.devcken.boot.querydsl.QEmployee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class EmployeeRepository {
	@Autowired
	private SQLQueryFactory queryFactory;

	public List findAll() {
		QEmployee qEmployeeEntity = QEmployee.Employee;

		return queryFactory.select(Projections.bean(EmployeeEntity.class, qEmployeeEntity.all())).from(qEmployeeEntity).fetch();
	}

	@Transactional
	public EmployeeEntity save(EmployeeEntity employeeEntity) {
		QEmployee employee = QEmployee.Employee;

		if (employeeEntity.getId() == null) {
			queryFactory.insert(employee)
					.populate(employeeEntity)
					.execute();
		} else {
			queryFactory.update(employee)
					.populate(employeeEntity)
					.execute();
		}

		return employeeEntity;
	}
}
