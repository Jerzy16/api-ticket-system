package com.fiberplus.main.entities;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "columns")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ColumnEntity {
    private String boardId;
    private String name;
    private Integer order;
}
