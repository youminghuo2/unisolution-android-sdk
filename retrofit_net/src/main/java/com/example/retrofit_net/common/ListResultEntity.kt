package com.example.retrofit_net.common

data class ListResultEntity<T>(
    val pageNum: Int,
    val pageSize: Int,
    val totalPage: Int,
    val totalCount: Int,
    val dataList: MutableList<T>,
)