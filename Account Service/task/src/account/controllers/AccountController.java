package account.controllers;

import account.dto.EmployeeSendDTO;
import account.entities.Employee;
import account.entities.User;
import account.exceptions.AccountServiceException;
import account.services.EmployeeService;
import account.dto.EmployeeReceiveDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@RestController
public class AccountController {

    @Autowired
    EmployeeService employeeService ;

    @GetMapping("/api/empl/payment")
    public Object getPayroll(@RequestParam(required = false) String period) {
//    public Object getPayroll(@RequestParam(required = false) @Pattern(regexp = "(0[1-9]|1[0-2])-\\d{4}", message = "Wrong date in request!") String period) {
        // XXX
        // pattern annotation doesn't work

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        List<Employee> employees = employeeService.findByEmployee(user.getUsername());

        if (period == null) {

            List<EmployeeSendDTO> sendEmployees = new LinkedList<>();
            employees.sort(Comparator.comparing(Employee::getPeriod).reversed());
            for (var employee : employees) {
                EmployeeSendDTO sendEmployee = new EmployeeSendDTO(user.getName(), user.getLastname(),
                        employeeService.getPeriodString(employee.getPeriod()), employeeService.getSalaryString(employee.getSalary()));

                sendEmployees.add(sendEmployee);
            }

            return sendEmployees;
        } else {

            if (!period.matches("(0[1-9]|1[0-2])-\\d{4}")) {
                throw new AccountServiceException("Wrong date in request!");
            }

            for (var employee : employees) {
                if (period.equals(employee.getPeriod())) {
                    return new EmployeeSendDTO(user.getName(), user.getLastname(), employeeService.getPeriodString(employee.getPeriod()),
                            employeeService.getSalaryString(employee.getSalary()));
                }
            }

            return Map.of("status", "Error! No information for this period.");
        }
    }

    @PutMapping("/api/acct/payments")
    public Map<String, Object> changeSalary(@Valid @RequestBody EmployeeReceiveDTO receiveEmployee) {

        List<Employee> employees = employeeService.findByEmployee(receiveEmployee.getEmployee().toLowerCase());
        for (var employee : employees) {
            if (receiveEmployee.getPeriod().equals(employee.getPeriod())) {
                employee.setEmployee(receiveEmployee.getEmployee().toLowerCase());
                employee.setPeriod(receiveEmployee.getPeriod());
                employee.setSalary(receiveEmployee.getSalary());
                employeeService.save(employee);

                return Map.of("status", "Updated successfully!");
            }
        }

        return Map.of("status", "Error! No information for this period.");
    }

    @PostMapping("/api/acct/payments")
    public Map<String, Object> uploadsPayrolls(@RequestBody List<@Valid EmployeeReceiveDTO> receiveEmployee) {

        // XXX
        // for this JSON (error in "period":"13-2021")
        // [{"employee":"johndoe@acme.com","period":"13-2021","salary":123456}]
        // @Pattern didn't check value of 'period' field
        // but the field check is triggered when writing to the database object of Employee class.

        employeeService.checkDuplicates(receiveEmployee);

        for (var newEmployee : receiveEmployee) {
            Employee employee = new Employee();
            employee.setEmployee(newEmployee.getEmployee().toLowerCase());
            employee.setPeriod(newEmployee.getPeriod());
            employee.setSalary(newEmployee.getSalary());
            employeeService.save(employee);
        }

        return Map.of("status", "Added successfully!");
    }
}
