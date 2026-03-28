package com.jumarkot.contracts.common;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PageRequest {
    int page;
    int size;
    String sortBy;
    String sortDirection;
}
