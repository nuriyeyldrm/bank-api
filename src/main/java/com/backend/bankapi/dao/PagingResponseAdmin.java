package com.backend.bankapi.dao;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class PagingResponseAdmin {

    private Long count;

    private Long pageNumber;

    private Long pageSize;

    private Long pageOffset;

    private Long pageTotal;

    private List<SearchDao> elements;
}
