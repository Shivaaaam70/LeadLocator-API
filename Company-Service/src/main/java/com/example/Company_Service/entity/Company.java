package com.example.Company_Service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "companies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String ycId;

    private String name;

    @Lob
    private String description;

    private String homepage;
    private String domain;

    private Integer teamSize;

    @Column(length = 2000)
    private String tags;

    private String locations;

    private boolean hiringFlag;
    private boolean topFlag;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String rawJson;

}
