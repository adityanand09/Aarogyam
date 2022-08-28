package com.busi.adi.aarogyam.Model;

import java.util.List;

public class HistoryItem {
    public String file_name;
    public List<IssueInfo> info_list;

    public HistoryItem(String file_name, Object info){
        this.file_name = file_name;
        this.info_list = (List<IssueInfo>)info;
    }

    public String getFile_name() {
        return file_name;
    }

    public List<IssueInfo> getInfo() {
        return info_list;
    }
}
