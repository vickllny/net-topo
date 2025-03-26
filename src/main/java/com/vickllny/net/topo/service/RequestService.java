package com.vickllny.net.topo.service;

import com.vickllny.net.topo.protocol.SNMPProtocol;
import com.vickllny.net.topo.protocol.SNMPV1;
import com.vickllny.net.topo.protocol.SNMPV3;
import org.apache.commons.lang.StringUtils;
import org.snmp4j.*;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.MPv3;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.*;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import java.io.IOException;

public class RequestService implements IRequestService {

    @Override
    public Object request(SNMPProtocol snmp) {
        if(snmp instanceof SNMPV1){
            return requestV1(snmp);
        }else if(snmp instanceof SNMPV3){
            return requestV3(snmp);
        }
        throw new RuntimeException("不支持的协议");
    }

    private Object requestV3(SNMPProtocol snmpProtocol) {
        Snmp snmp = null;
        try {
            final SNMPV3 snmpv3 = (SNMPV3) snmpProtocol;
            TransportMapping<?> transport = new DefaultUdpTransportMapping();
            snmp = new Snmp(transport);

            USM usm = new USM(SecurityProtocols.getInstance(), new OctetString(MPv3.createLocalEngineID()), 0);
            SecurityModels.getInstance().addSecurityModel(usm);

            // 添加用户
            snmp.getUSM().addUser(
                    new OctetString(snmpv3.getUsername()),
                    new UsmUser(
                            new OctetString(snmpv3.getUsername()),
                            snmpv3.getAuthProtocol().getID(), new OctetString(snmpv3.getAuthPassword()),
                            snmpv3.getPrivacyProtocol().getID(), new OctetString(snmpv3.getPrivacyPassword())
                    )
            );

            transport.listen();

            // 设置目标
            UserTarget target = new UserTarget();
            target.setAddress(GenericAddress.parse("udp:" + snmpv3.getIp() + "/" + snmpv3.getPort()));
            target.setRetries(2);
            target.setTimeout(1500);
            target.setVersion(SnmpConstants.version3);
            target.setSecurityLevel(snmpv3.getLevel().getSnmpValue());
            target.setSecurityName(new OctetString(snmpv3.getUsername()));

            // 构造 PDU
            ScopedPDU pdu = new ScopedPDU();
            pdu.add(new VariableBinding(new OID(snmpv3.getOid())));
            pdu.setType(PDU.GET);

            // 发送请求
            ResponseEvent response = snmp.send(pdu, target);
            if (response.getResponse() != null) {
                System.out.println("SNMP v3 响应: " + response.getResponse().getVariableBindings());
                return response.getResponse().getVariableBindings();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(snmp != null){
                try {
                    snmp.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private Object requestV1(SNMPProtocol snmpProtocol) {
        Snmp snmp = null;
        try {
            final SNMPV1 snmpv1 = (SNMPV1) snmpProtocol;
            // 初始化 SNMP 传输
            TransportMapping<?> transport = new DefaultUdpTransportMapping();
            snmp = new Snmp(transport);
            transport.listen(); // 启动监听

            // 创建 SNMP 目标
            CommunityTarget target = new CommunityTarget();
            target.setAddress(GenericAddress.parse("udp:" + snmpv1.getIp() + "/" + snmpv1.getPort()));
            target.setCommunity(new OctetString(StringUtils.isNotBlank(snmpv1.getWriteCommunity()) ? snmpv1.getWriteCommunity() : snmpv1.getReadCommunity()));
            target.setRetries(2);
            target.setTimeout(1500);
            target.setVersion(SnmpConstants.version2c); // 切换成 version1 也可以

            // 构造 PDU
            PDU pdu = new PDU();
            pdu.add(new VariableBinding(new OID(snmpv1.getOid())));
            pdu.setType(PDU.GET);  // 也可以用 PDU.GETNEXT

            // 发送请求
            ResponseEvent response = snmp.send(pdu, target);
            if (response.getResponse() != null) {
                System.out.println("SNMP v2c 响应: " + response.getResponse().getVariableBindings());
                return response.getResponse().getVariableBindings();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(snmp != null){
                try {
                    snmp.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
        return null;
    }


}
