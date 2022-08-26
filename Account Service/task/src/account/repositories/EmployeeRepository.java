package account.repositories;

import account.entities.Employee;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.LinkedList;
import java.util.List;

@Repository
public interface EmployeeRepository extends CrudRepository<Employee, Long> {
    Employee findEmployeeById(Long id);

    Employee findAllById(Long id);
    List<Employee> findByEmployee(@Param("employee") String employee);
    Employee findEmployeeByEmployee(String employee);
}
