package com.sharebravo.bravo.model.parameters;

import java.util.List;

import org.apache.http.NameValuePair;

public class PaPutSNS extends BasicParameter{
    String foreignID;
    String foreignSNS;
    String foreignAccessToken;

    public PaPutSNS() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public List<NameValuePair> createNameValuePair() {
        // TODO Auto-generated method stub
        return null;
    }
}