package com.jumarkot.contracts.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

/**
 * Generic paginated response wrapper used by all Jumarkot list endpoints.
 *
 * <p>Cursor-based pagination is used rather than offset pagination to ensure stable results
 * under concurrent writes. The {@code nextCursor} field is an opaque, base64-encoded token
 * that should be passed as the {@code cursor} query parameter in the next page request.
 *
 * <pre>{@code
 * GET /v1/tenants?limit=20
 * →
 * {
 *   "data": [...],
 *   "totalElements": 142,
 *   "pageSize": 20,
 *   "hasNext": true,
 *   "nextCursor": "eyJpZCI6IjEyMyJ9"
 * }
 * }</pre>
 *
 * @param <T> the type of elements in this page
 */
@Data
@Builder
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageResponse<T> {

    /**
     * The elements on the current page. Never null; may be empty on the last page.
     */
    private final List<T> data;

    /**
     * Total number of elements matching the query across all pages.
     * May be {@code null} when an exact count is prohibitively expensive (e.g. large datasets).
     */
    private final Long totalElements;

    /**
     * Number of elements requested per page (the {@code limit} parameter).
     */
    private final int pageSize;

    /**
     * Whether a subsequent page of results is available.
     */
    private final boolean hasNext;

    /**
     * Opaque cursor token to retrieve the next page. Present only when {@link #hasNext}
     * is {@code true}. Clients must treat this as an opaque string.
     */
    private final String nextCursor;

    /**
     * Convenience factory for an empty page response.
     *
     * @param <T> element type
     * @return empty {@link PageResponse} with {@code hasNext = false} and zero counts
     */
    public static <T> PageResponse<T> empty() {
        return PageResponse.<T>builder()
                .data(List.of())
                .totalElements(0L)
                .pageSize(0)
                .hasNext(false)
                .build();
    }

    /**
     * Convenience factory for a single-page response containing all provided elements.
     *
     * @param <T>  element type
     * @param data the complete list of elements
     * @return {@link PageResponse} wrapping all elements with {@code hasNext = false}
     */
    public static <T> PageResponse<T> of(List<T> data) {
        return PageResponse.<T>builder()
                .data(data)
                .totalElements((long) data.size())
                .pageSize(data.size())
                .hasNext(false)
                .build();
    }
}
