package com.done.core.domain.models.pagination

data class Pagination(
    val total: Int,
    val count: Int,
    val perPage: Int,
    val currentPage: Int,
    val totalPages: Int
)