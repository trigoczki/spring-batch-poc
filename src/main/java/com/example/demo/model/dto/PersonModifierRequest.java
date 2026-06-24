package com.example.demo.model.dto;

import jakarta.validation.constraints.NotNull;

public record PersonModifierRequest(@NotNull Long personId) {

}
