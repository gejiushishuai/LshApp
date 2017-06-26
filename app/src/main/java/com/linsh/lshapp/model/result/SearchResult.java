package com.linsh.lshapp.model.result;

import java.util.List;

/**
 * Created by Senh Linsh on 17/6/26.
 */

public class SearchResult {

    public CharSequence personName;
    public CharSequence personDesc;
    public List<CharSequence> typeDetail;

    public SearchResult(CharSequence personName) {
        this.personName = personName;
    }

    public SearchResult(CharSequence personName, CharSequence personDesc) {
        this.personName = personName;
        this.personDesc = personDesc;
    }

    public SearchResult(CharSequence personName, List<CharSequence> typeDetail) {
        this.personName = personName;
        this.typeDetail = typeDetail;
    }
}
