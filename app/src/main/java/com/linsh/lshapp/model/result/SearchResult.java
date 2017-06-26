package com.linsh.lshapp.model.result;

import java.util.List;

/**
 * Created by Senh Linsh on 17/6/26.
 */

public class SearchResult {

    public String personId;
    public CharSequence personName;
    public CharSequence personDesc;
    public List<CharSequence> typeDetail;

    public SearchResult(String personId, CharSequence personName) {
        this.personId = personId;
        this.personName = personName;
    }

    public SearchResult(String personId, CharSequence personName, CharSequence personDesc) {
        this.personId = personId;
        this.personName = personName;
        this.personDesc = personDesc;
    }

    public SearchResult(String personId, CharSequence personName, List<CharSequence> typeDetail) {
        this.personId = personId;
        this.personName = personName;
        this.typeDetail = typeDetail;
    }
}
