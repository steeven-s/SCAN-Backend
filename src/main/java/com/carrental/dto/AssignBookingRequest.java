
package com.carrental.dto;

import jakarta.validation.constraints.NotBlank;

public record AssignBookingRequest(

        @NotBlank(message = "Assigned employee is required")
        String assignedTo

) {}
