package com.example.backend.dto.custom;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Standard paginated response wrapper for list-based API endpoints.
 *
 * @param <T> The type of each item in the content list.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PagedResponse<T> {

    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean last;
}
