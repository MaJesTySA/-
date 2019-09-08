package com.inside.mapper;

import com.inside.pojo.SearchRecords;
import com.inside.utils.MyMapper;

import java.util.List;

public interface SearchRecordsMapper extends MyMapper<SearchRecords> {
    List<String> getHotWords();
}