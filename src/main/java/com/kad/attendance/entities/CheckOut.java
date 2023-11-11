package com.kad.attendance.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "check_out")
public class CheckOut extends BaseEntity{
    @Id
    @GeneratedValue
    private Long id;

    private Double latitude;

    private Double longitude;

    private String date;

    private String month;

    private String year;

    private String time;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
}
