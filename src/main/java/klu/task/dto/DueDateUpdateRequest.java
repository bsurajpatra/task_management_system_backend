package klu.task.dto;

import jakarta.validation.constraints.FutureOrPresent;

import java.time.LocalDateTime;

public class DueDateUpdateRequest {

    // If null, due date will be cleared
    @FutureOrPresent(message = "Due date must be now or in the future")
    private LocalDateTime dueDate;

    public LocalDateTime getDueDate() { return dueDate; }
    public void setDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; }
}


