package com.kad.attendance.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "check_in")
public class CheckIn extends BaseEntity{
    @Id
    @GeneratedValue
    private Long id;

    private Double latitude;

    private Double longitude;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @JsonBackReference
    private User user;

    @PrePersist
    public void beforePersist() {
        setCreatedAt(new Date());
    }
}
