package com.vickllny;

import com.vickllny.net.topo.constants.OIDS;
import com.vickllny.net.topo.protocol.SNMPV1;
import com.vickllny.net.topo.service.IRequestService;
import com.vickllny.net.topo.service.RequestService;
import org.junit.Test;

public class JTests {

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
}
