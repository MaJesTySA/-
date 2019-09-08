package com.inside.pojo;

import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "search_records")
public class SearchRecords {
    @Id
    private String id;

    private String content;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}