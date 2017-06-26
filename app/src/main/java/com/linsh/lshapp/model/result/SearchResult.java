package com.linsh.lshapp.model.result;

import java.util.List;

/**
 * Created by Senh Linsh on 17/6/26.
 */

public class SearchResult {

    public String personName;
    public String personDesc;
    public List<String> typeDetail;

    public SearchResult(String personName) {
        this.personName = personName;
    }

    public SearchResult(String personName, String personDesc) {
        this.personName = personName;
        this.personDesc = personDesc;
    }

    public SearchResult(String personName, List<String> typeDetail) {
        this.personName = personName;
        this.typeDetail = typeDetail;
    }
}
