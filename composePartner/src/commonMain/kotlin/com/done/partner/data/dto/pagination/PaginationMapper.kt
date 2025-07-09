package com.done.partner.data.dto.pagination

import com.done.core.domain.models.pagination.Pagination


fun PaginationDto.toPagination(): Pagination {
    return Pagination(
        total = total ?: 0,
        count = count ?: 0,
        perPage = perPage ?: 0,
        currentPage = currentPage ?: 0,
        totalPages = totalPages ?: 0
    )
}