package com.vickllny.net.topo.protocol;

import org.snmp4j.security.*;
import org.snmp4j.smi.OID;

public enum AuthProtocol {
    SHA(AuthSHA.ID),
    MD5(AuthMD5.ID),
    HMAC128SHA224(AuthHMAC128SHA224.ID),
    HMAC192SHA256(AuthHMAC192SHA256.ID),
    HMAC256SHA384(AuthHMAC256SHA384.ID),
    HMAC384SHA512(AuthHMAC384SHA512.ID),
    ;

    private final OID oid;

    AuthProtocol(OID oid) {
        this.oid = oid;
    }

    public OID getOid() {
        return oid;
    }
}
