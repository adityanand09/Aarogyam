package com.busi.adi.aarogyam.Model;

public class AilmentInfo {
    public String fileName;
    public AilmentRecord record;

    public AilmentInfo() {
    }

    public AilmentInfo(String fileName, AilmentRecord record) {
        this.fileName = fileName;
        this.record = record;
    }

    public String getFileName() {
        return fileName;
    }

    public AilmentRecord getRecord() {
        return record;
    }
}
