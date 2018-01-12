/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.example.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@JsonPropertyOrder({"ID", "Description", "Owner"})
@Getter
@Setter
@EqualsAndHashCode
public class TodoItem {
    @JsonProperty("Description")
    private String description;
    @JsonProperty("ID")
    private int id;
    @JsonProperty("Owner")
    private String owner;

    @JsonCreator
    public TodoItem(
            @JsonProperty("ID") int id,
            @JsonProperty("Description") String description,
            @JsonProperty("Owner") String owner
    ) {
        this.description = description;
        this.id = id;
        this.owner = owner;
    }
}

