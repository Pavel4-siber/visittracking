package com.example.visittracking.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * @author Pavel Zhurenkov
 */
@Entity
@Table(name = "doctors")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "time_zone")
    private String timezone;

}
