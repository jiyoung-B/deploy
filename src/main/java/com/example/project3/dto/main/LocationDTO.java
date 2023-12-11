package com.example.project3.dto.main;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
public class LocationDTO {

    private Long location_id;
    private String location_name;
    private double latitude;
    private double longitude;
    private Date location_date;

}
