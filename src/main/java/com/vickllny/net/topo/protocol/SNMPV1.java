package com.vickllny.net.topo.protocol;

import org.snmp4j.Target;

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

    @Override
    public Target target() {
        return null;
    }
}
