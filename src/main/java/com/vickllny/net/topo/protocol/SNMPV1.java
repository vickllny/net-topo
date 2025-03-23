package com.vickllny.net.topo.protocol;

public class SNMPV1 extends SNMPProtocol {

    protected String readCommunity;
    protected String writeCommunity;

    public String getReadCommunity() {
        return readCommunity;
    }

    public void setReadCommunity(String readCommunity) {
        this.readCommunity = readCommunity;
    }

    public String getWriteCommunity() {
        return writeCommunity;
    }

    public void setWriteCommunity(String writeCommunity) {
        this.writeCommunity = writeCommunity;
    }
}
