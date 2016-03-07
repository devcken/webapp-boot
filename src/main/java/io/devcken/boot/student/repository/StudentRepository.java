package io.devcken.boot.student.repository;

import io.devcken.boot.student.entity.StudentEntity;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends GraphRepository<StudentEntity> {
}
