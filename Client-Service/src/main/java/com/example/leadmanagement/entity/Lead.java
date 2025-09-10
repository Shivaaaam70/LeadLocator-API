package com.example.leadmanagement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "leads", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lead {

    @Id
    @Column(length = 36, updatable = false,nullable = false)
    private String id;

    @NotBlank(message = "Name is required")
    @Size(min=2 , message = "Name must have at least 2 character")
    private String name;

    @Email(message = "Invalid format")
    @NotBlank(message = "Email is required")
    @Column(nullable = false, unique = true)
    private String email;

    private String location;

    private String description;

    private String requirement;

    private String broughtBy;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

}
