package com.vickllny;

import com.vickllny.net.topo.constants.OIDS;
import com.vickllny.net.topo.protocol.SNMPV1;
import com.vickllny.net.topo.protocol.SNMPV3;
import com.vickllny.net.topo.service.IRequestService;
import com.vickllny.net.topo.service.RequestService;
import org.junit.Test;
import org.snmp4j.security.*;

public class JTests {

//    noAuthNoPriv（无认证无加密）
//    authNoPriv（有认证无加密）
//    authPriv（有认证和加密）


    @Test
    public void test(){
        System.out.print("nice");
    }

    @Test
    public void test1(){
        final IRequestService requestService = new RequestService();
        final SNMPV1 snmpv1 = new SNMPV1();
        snmpv1.setIp("192.168.190.130");
        snmpv1.setPort(161);
        snmpv1.setOid(OIDS.SYSTEM_NAME);
        snmpv1.setReadCommunity("public");
        final Object object = requestService.request(snmpv1);
        System.out.print(object);
    }

    @Test
    public void test2(){
        final IRequestService requestService = new RequestService();
        final SNMPV1 snmpv1 = new SNMPV1();
        snmpv1.setIp("192.168.190.130");
        snmpv1.setPort(161);
        snmpv1.setOid(OIDS.SYSTEM_UP_TIME);
        snmpv1.setReadCommunity("public");
        final Object object = requestService.request(snmpv1);
        System.out.print(object);
    }

    @Test
    public void test3(){
        final IRequestService requestService = new RequestService();
        final SNMPV1 snmpv1 = new SNMPV1();
        snmpv1.setIp("192.168.190.130");
        snmpv1.setPort(161);
        snmpv1.setOid(OIDS.IF_TABLE);
        snmpv1.setReadCommunity("public");
        final Object object = requestService.request(snmpv1);
        System.out.print(object);
    }

    @Test
    public void test4(){
        final IRequestService requestService = new RequestService();
        final SNMPV1 snmpv1 = new SNMPV1();
        snmpv1.setIp("192.168.190.130");
        snmpv1.setPort(161);
        snmpv1.setOid(OIDS.IF_NUMBER);
        snmpv1.setReadCommunity("public");
        final Object object = requestService.request(snmpv1);
        System.out.print(object);
    }

    @Test
    public void test5(){
        final IRequestService requestService = new RequestService();
        final SNMPV3 snmpv3 = new SNMPV3();
        snmpv3.setIp("192.168.110.130");
        snmpv3.setPort(161);
        snmpv3.setOid(OIDS.IF_NUMBER);
        snmpv3.setUsername("zouq");
        snmpv3.setLevel(SecurityLevel.authPriv);
        snmpv3.setAuthProtocol(new AuthMD5());
        snmpv3.setPrivacyProtocol(new PrivDES());
        snmpv3.setAuthPassword("qaq123456-");
        snmpv3.setPrivacyPassword("qaq123456-");
        final Object object = requestService.request(snmpv3);
        System.out.print(object);
    }
}
