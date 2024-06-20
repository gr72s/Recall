package cc.green.recall.server

import org.springframework.data.domain.Page

class Pages<T>(page: Page<*>, val content: List<T>) {
    val totalPage = page.totalPages
    val totalSize = page.totalElements
    val currentPage = page.pageable.pageNumber + 1
    val currentSize = page.pageable.pageSize
}