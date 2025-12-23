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
public class BoardEntity extends BaseEntity {
    private String name;
    private String description;
    private String createdBy;
    private String status;

}
