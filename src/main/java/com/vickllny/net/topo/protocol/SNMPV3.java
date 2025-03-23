package com.vickllny.net.topo.protocol;

public class SNMPV3 extends SNMPProtocol {

    protected EncryptionLevel level;
    protected AuthProtocol authProtocol;
    protected EncryptionType encryptionType;
    protected String username;
    protected String password;

    public EncryptionLevel getLevel() {
        return level;
    }

    public void setLevel(EncryptionLevel level) {
        this.level = level;
    }

    public AuthProtocol getAuthProtocol() {
        return authProtocol;
    }

    public void setAuthProtocol(AuthProtocol authProtocol) {
        this.authProtocol = authProtocol;
    }

    public EncryptionType getEncryptionType() {
        return encryptionType;
    }

    public void setEncryptionType(EncryptionType encryptionType) {
        this.encryptionType = encryptionType;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
