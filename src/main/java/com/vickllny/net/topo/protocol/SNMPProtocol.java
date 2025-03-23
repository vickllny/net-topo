package com.vickllny.net.topo.protocol;

public abstract class SNMPProtocol {

    protected String ip;
    protected int port;
    protected String oid;

    public SNMPProtocol() {
    }

    public SNMPProtocol(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }
}
