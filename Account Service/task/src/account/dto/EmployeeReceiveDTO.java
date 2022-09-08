package account.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class EmployeeReceiveDTO {

    @NotBlank(message = "Empty employee field!")
    private String employee;

    @NotBlank(message = "Empty period field!")
    @Pattern(regexp = "(0[1-9]|1[0-2])-\\d{4}", message = "Wrong period field!")
    private String period;
    @Min(value = 0, message = "Salary cannot be negative")
    private long salary;

    public EmployeeReceiveDTO() {
    }

    public EmployeeReceiveDTO(String employee, String period, long salary) {
        this.employee = employee;
        this.period = period;
        this.salary = salary;
    }

    public String getEmployee() {
        return employee;
    }

    public void setEmployee(String employee) {
        this.employee = employee;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public long getSalary() {
        return salary;
    }

    public void setSalary(long salary) {
        this.salary = salary;
    }
}
