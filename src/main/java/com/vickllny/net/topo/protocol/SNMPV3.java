package com.vickllny.net.topo.protocol;

import org.snmp4j.security.AuthenticationProtocol;
import org.snmp4j.security.PrivacyProtocol;
import org.snmp4j.security.SecurityLevel;

public class SNMPV3 extends SNMPProtocol {

    protected SecurityLevel level;
    protected AuthenticationProtocol authProtocol;
    protected PrivacyProtocol privacyProtocol;
    protected String username;
    protected String authPassword;
    protected String privacyPassword;

    public SecurityLevel getLevel() {
        return level;
    }

    public void setLevel(SecurityLevel level) {
        this.level = level;
    }

    public AuthenticationProtocol getAuthProtocol() {
        return authProtocol;
    }

    public void setAuthProtocol(AuthenticationProtocol authProtocol) {
        this.authProtocol = authProtocol;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAuthPassword() {
        return authPassword;
    }

    public void setAuthPassword(String authPassword) {
        this.authPassword = authPassword;
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
