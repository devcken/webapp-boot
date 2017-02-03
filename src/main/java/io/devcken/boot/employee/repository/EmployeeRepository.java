package io.devcken.boot.employee.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.sql.SQLQueryFactory;
import io.devcken.boot.employee.entity.Employee;
import io.devcken.boot.querydsl.QEmployee;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.util.List;

@Repository
public class EmployeeRepository {
	private final SQLQueryFactory queryFactory;

	@Inject
	public EmployeeRepository(SQLQueryFactory queryFactory) {
		this.queryFactory = queryFactory;
	}

	public List<Employee> findAll() {
		QEmployee qEmployeeEntity = QEmployee.Employee;

		return queryFactory
				.select(Projections.bean(Employee.class, qEmployeeEntity.all()))
				.from(qEmployeeEntity)
				.fetch();
	}

	public Employee save(Employee employeeEntity) {
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
