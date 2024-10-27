package com.app.myapp.user;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.springframework.data.domain.Sort;

@Data
public class UserRequestParams {

    private String searchTerm = "";

    @Pattern(regexp = "username|email", message = "Sort field must be either 'username' or 'email'")
    private String sortField = "username";

    @Pattern(regexp = "asc|desc", message = "Sort direction must be 'asc' or 'desc'")
    private String sortDirection = "asc";

    @Min(value = 0, message = "Page number must be 0 or greater")
    private int page = 0;

    @Min(value = 1, message = "Page size must be 1 or greater")
    private int size = 10;

    public Sort getSortOrder() {
        return sortDirection.equalsIgnoreCase("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();
    }
}
