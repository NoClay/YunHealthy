package indi.noclay.cloudhealth.carddata;

import java.io.Serializable;
import java.util.List;

/**
 * Created by NoClay on 2018/5/2.
 */

public abstract class ShowApiRetBase implements Serializable{
    public Number page;
    public String limit;
    public String allResults;
    public Integer ret_code;
    public String msg;

    public Number getPage() {
        return page;
    }

    public void setPage(Number page) {
        this.page = page;
    }

    public String getLimit() {
        return limit;
    }

    public void setLimit(String limit) {
        this.limit = limit;
    }

    public String getAllResults() {
        return allResults;
    }

    public void setAllResults(String allResults) {
        this.allResults = allResults;
    }

    public Integer getRet_code() {
        return ret_code;
    }

    public void setRet_code(Integer ret_code) {
        this.ret_code = ret_code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public abstract List<Object> getDataItem();
}
