package account.services;

import account.entities.Employee;
import account.exceptions.AccountServiceException;
import account.repositories.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public Employee findEmployeeById(Long id) {
        return employeeRepository.findEmployeeById(id);
    }

    public Employee findEmployeeByEmployee(String employee) {
        return employeeRepository.findEmployeeByEmployee(employee);
    }

    public List<Employee> findByEmployee(@Param("employee") String employee) {
        return employeeRepository.findByEmployee(employee);
    }

    public Employee save(Employee toSave) {
        return employeeRepository.save(toSave);
    }

    public String getPeriodString(String period) {
        String [] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        String[] periodParts = period.split("-");

        StringBuilder sb = new StringBuilder();
        sb.append(months[Integer.parseInt(periodParts[0]) - 1]);
        sb.append("-");
        sb.append(periodParts[1]);

        return sb.toString();
    }

    public String getSalaryString(long salary) {
        StringBuilder sb = new StringBuilder();
        sb.append(salary / 100);
        sb.append(" dollar(s) ");
        sb.append(salary % 100);
        sb.append(" cent(s)");

        return sb.toString();
    }

    public void checkDuplicates(List<ReceiveEmployee> employees) {

        class EmployeeDuplicates {
            private String employee;
            private String period;

            public EmployeeDuplicates(String employee, String period) {
                this.employee = employee;
                this.period = period;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;

                EmployeeDuplicates that = (EmployeeDuplicates) o;

                if (!employee.equals(that.employee)) return false;
                return period.equals(that.period);
            }

            @Override
            public int hashCode() {
                int result = employee.hashCode();
                result = 31 * result + period.hashCode();
                return result;
            }
        }

        Set<EmployeeDuplicates> addedEmployees = new HashSet<>();
        for (var employee : employees) {
            EmployeeDuplicates duplicate = new EmployeeDuplicates(employee.getEmployee(), employee.getPeriod());
            if (addedEmployees.contains(duplicate)) {
                throw new AccountServiceException("Duplicated entry in payment list!");
            }
            addedEmployees.add(duplicate);
        }
    }
}
