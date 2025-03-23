package com.vickllny.net.topo.protocol;

public class SNMPV3 extends SNMPProtocol {

    protected EncryptionLevel level;
    protected AuthProtocol authProtocol;
    protected PrivacyProtocol privacyProtocol;
    protected String username;
    protected String password;
    protected String privacyPassword;

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

    public PrivacyProtocol getEncryptionType() {
        return privacyProtocol;
    }

    public void setEncryptionType(PrivacyProtocol privacyProtocol) {
        this.privacyProtocol = privacyProtocol;
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

    public PrivacyProtocol getPrivacyProtocol() {
        return privacyProtocol;
    }

    public void setPrivacyProtocol(PrivacyProtocol privacyProtocol) {
        this.privacyProtocol = privacyProtocol;
    }

    public String getPrivacyPassword() {
        return privacyPassword;
    }

    public void setPrivacyPassword(String privacyPassword) {
        this.privacyPassword = privacyPassword;
    }
}
