package com.fiberplus.main.entities;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "boards")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SlaEntity extends BaseEntity {
    private String priority; // HIGH, CRITICAL
    private Integer maxHours;
}
