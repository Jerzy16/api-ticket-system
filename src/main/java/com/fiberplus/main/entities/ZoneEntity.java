package com.fiberplus.main.entities;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "zones")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ZoneEntity extends BaseEntity {
    private String name;
    private String district;
    private String description;
    private String status;
}
