package com.vickllny.net.topo.protocol;

import org.snmp4j.smi.OID;
import org.snmp4j.security.*;

public enum PrivacyProtocol {
    PRIV3DES(Priv3DES.ID, "3DES"),
    PRIVAES128(PrivAES128.ID, "AES128"),
    PRIVAES192(PrivAES192.ID, "AES192"),
    PRIVAES256(PrivAES256.ID, "AES256"),
    PRIVDES(PrivDES.ID, "DES"),
    ;

    private final OID oid;
    private final String name;


    PrivacyProtocol(OID oid, String name) {
        this.oid = oid;
        this.name = name;
    }

    public OID getOid() {
        return oid;
    }

    public String getName() {
        return name;
    }
}
