package sc.snicky.springbootjwtauth.api.v1.admin.dtos.responses;

import java.util.List;

public record PageResponse<T>(
    List<T> content,
    int page,
    int size,
    long totalElements,
    int totalPages
) {
}
