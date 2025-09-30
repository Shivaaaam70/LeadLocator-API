package com.example.Onboard_Service.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "onboard")
public class Onboard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private long phoneNo;
    private String location;
    private String workplaceType;
    private String employmentType;
    private String field;
    private String onboarded_By;
    private int experience;
    private String company_name;
    private String skills;
    private String status;

}
