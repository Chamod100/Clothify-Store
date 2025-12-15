package org.example.Model.DTO;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class EmployeeDTO {

    private String empId;
    private String name;
    private String title;
    private String nic;
    private String address;
    private String dob;
    private String contact;
    private String bankAccountNo;
    private String bankBranch;
}
