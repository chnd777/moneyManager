package com.budget.moneymanager;

import androidx.recyclerview.widget.RecyclerView;

public class ListData {
    private String lAmt;
    private String lKeyword;
    private String lDate;
    private RecyclerView data;
    private String id;

    public ListData(String lamt, String lkeyword, String ldate) {
        this.lAmt = lamt;
        this.lKeyword = lkeyword;
        this.lDate = ldate;
    }

    public ListData(String lamt, String lkeyword, String ldate, String id) {
        this.lAmt = lamt;
        this.lKeyword = lkeyword;
        this.lDate = ldate;
        this.id =id;
    }

    public void setlAmt(String lAmt) {
        this.lAmt = lAmt;
    }
    public void setId(String id) {
        this.id = id;
    }
    public void setlKeyword(String lKeyword) {
        this.lKeyword = lKeyword;
    }
    public void setData(RecyclerView data) {
        this.data = data;
    }
    public void setlDate(String lDate) {
        this.lDate = lDate;
    }
    public String getlKeyword() {
        return lKeyword;
    }
    public String getlAmt() {
        return lAmt;
    }
    public RecyclerView getData() { return data; }
    public String getId() { return id; }
    public String getlDate() {
        return lDate;
    }
}
