package com.sharebravo.bravo.model.parameters;

import java.util.List;

import org.apache.http.NameValuePair;

public class PaGetFollowHistory extends BasicParameter{
    int    start;
    String maxFollowID;

    public PaGetFollowHistory() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public List<NameValuePair> createNameValuePair() {
        // TODO Auto-generated method stub
        return null;
    }
}