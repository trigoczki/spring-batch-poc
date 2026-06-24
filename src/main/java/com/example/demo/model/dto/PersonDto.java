package com.example.demo.model.dto;

import jakarta.validation.constraints.NotBlank;

public record PersonDto(Long Id, @NotBlank String name, @NotBlank String address,
                        @NotBlank String occupation) {

}
