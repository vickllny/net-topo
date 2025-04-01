package com.vickllny.net.topo.protocol;

import org.snmp4j.Target;
import org.snmp4j.UserTarget;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.AuthenticationProtocol;
import org.snmp4j.security.PrivacyProtocol;
import org.snmp4j.security.SecurityLevel;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OctetString;

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

    @Override
    public Target target() {
        UserTarget target = new UserTarget();
        target.setAddress(GenericAddress.parse("udp:" + getIp() + "/" + getPort()));
        target.setRetries(2);
        target.setTimeout(1500);
        target.setVersion(SnmpConstants.version3);
        target.setSecurityLevel(getLevel().getSnmpValue());
        target.setSecurityName(new OctetString(getUsername()));

        return target;
    }
}
