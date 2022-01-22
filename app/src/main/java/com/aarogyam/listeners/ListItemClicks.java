package com.aarogyam.listeners;

import com.aarogyam.Model.IssueInfo;

import java.util.List;

public interface ListItemClicks {
    void onListItemClicked(Object object);
    void onListItemLongClicked(String filePath, String fileName, int position);
}
