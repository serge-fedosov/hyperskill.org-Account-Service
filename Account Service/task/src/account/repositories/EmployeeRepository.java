package account.repositories;

import account.entities.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Employee findEmployeeById(Long id);
    List<Employee> findByEmployee(@Param("employee") String employee);
    Employee findEmployeeByEmployee(String employee);
}
