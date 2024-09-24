package com.cardanoj.jna.annotation.processor.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClassDefinition {
    private String packageName;
    private String name;
    private String objType;
    private int alternative;

    private boolean hasLombokAnnotation;

    private List<Field> fields = new ArrayList<>();
}

