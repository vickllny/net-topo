package com.vickllny;

import com.vickllny.net.topo.constants.OIDS;
import com.vickllny.net.topo.protocol.SNMPV3;
import org.snmp4j.*;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.MPv3;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.*;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import java.util.*;

public class SnmpGetInterfacesTests {
    public static void main(String[] args) {
//        String targetAddress = "udp:192.168.10.1/161";  // SNMP 设备 IP
        String targetAddress = "udp:192.168.190.130/161";  // SNMP 设备 IP
        String community = "public";  // 共同体字符串

        // 需要获取的 OID 前缀
        String ifIndexOid = "1.3.6.1.2.1.2.2.1.1";       // 端口索引
        String ifDescrOid = "1.3.6.1.2.1.2.2.1.2";       // 端口名称
        String ifPhysAddressOid = "1.3.6.1.2.1.2.2.1.6"; // MAC 地址
        String ifOperStatusOid = "1.3.6.1.2.1.2.2.1.8";  // 端口状态
        String ifInOctets = "1.3.6.1.2.1.2.2.1.10";  // 接口接收字节数
        String ifOutOctets = "1.3.6.1.2.1.2.2.1.16";  // 接口发送字节数
        String lldpMac = "1.0.8802.1.1.2.1.4.1.1.5";  //
        String lldpPort = "1.0.8802.1.1.2.1.4.1.1.8";  //
        String lldpSysname = "1.0.8802.1.1.2.1.4.1.1.9";  //

        final SNMPV3 snmpv3 = new SNMPV3();
        snmpv3.setIp("192.168.190.150");
        snmpv3.setPort(161);
        snmpv3.setOid(OIDS.IF_NUMBER);
        snmpv3.setUsername("zouq");
        snmpv3.setLevel(SecurityLevel.authPriv);
        snmpv3.setAuthProtocol(new AuthMD5());
        snmpv3.setPrivacyProtocol(new PrivDES());
        snmpv3.setAuthPassword("qaq123456-");
        snmpv3.setPrivacyPassword("qaq123456-");

        try {
            // 初始化 SNMP
            TransportMapping<UdpAddress> transport = new DefaultUdpTransportMapping();
            Snmp snmp = new Snmp(transport);
            transport.listen();

            // 创建目标
//            CommunityTarget target = new CommunityTarget();
//            target.setAddress(GenericAddress.parse(targetAddress));
//            target.setCommunity(new OctetString(community));
//            target.setVersion(SnmpConstants.version2c);
//            target.setTimeout(3000);
//            target.setRetries(2);

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

            UserTarget target = new UserTarget();
            target.setAddress(GenericAddress.parse("udp:" + snmpv3.getIp() + "/" + snmpv3.getPort()));
            target.setRetries(2);
            target.setTimeout(1500);
            target.setVersion(SnmpConstants.version3);
            target.setSecurityLevel(snmpv3.getLevel().getSnmpValue());
            target.setSecurityName(new OctetString(snmpv3.getUsername()));

            // 获取端口索引（ifIndex）
            List<String> interfaceIndexes = snmpWalk(snmp, target, ifIndexOid);
            List<String> interfaceIndexes1 = snmpWalk(snmp, target, lldpMac);
            List<String> interfaceIndexes2 = snmpWalk(snmp, target, lldpPort);
            List<String> interfaceIndexes3 = snmpWalk(snmp, target, lldpSysname);

            // 存储接口信息
            Map<String, String> interfaceNames = snmpWalkMap(snmp, target, ifDescrOid);
            Map<String, String> macAddresses = snmpWalkMap(snmp, target, ifPhysAddressOid);
            Map<String, String> operStatuses = snmpWalkMap(snmp, target, ifOperStatusOid);
            Map<String, String> receiveBytes = snmpWalkMap(snmp, target, ifInOctets);
            Map<String, String> sendBytes = snmpWalkMap(snmp, target, ifOutOctets);
            Map<String, String> lldpMacs = snmpWalkMap(snmp, target, lldpMac);
            Map<String, String> lldpPort1 = snmpWalkMap(snmp, target, lldpPort);
            Map<String, String> lldpSysname1 = snmpWalkMap(snmp, target, lldpSysname);


            Map<String, String> ipAddresses = snmpWalkIp(snmp, target);

            // 打印接口信息
            for (String index : interfaceIndexes) {
                String name = interfaceNames.getOrDefault(index, "Unknown");
                String mac = macAddresses.getOrDefault(index, "N/A");
                String ip = ipAddresses.getOrDefault(index, "N/A");
                String status = operStatuses.getOrDefault(index, "N/A");
                String receive = receiveBytes.getOrDefault(index, "N/A");
                String send = sendBytes.getOrDefault(index, "N/A");

                System.out.printf("接口索引: %s | 名称: %s | IP: %s | MAC: %s | in: %s | out: %s | 状态: %s%n",
                        index, name, ip, mac, receive, send, status.equals("1") ? "Up" : "Down");
            }

            snmp.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 执行 SNMP Walk，返回所有匹配的 OID 值
     */
    private static List<String> snmpWalk(Snmp snmp, AbstractTarget target, String oid) throws Exception {
        List<String> results = new ArrayList<>();
        OID targetOID = new OID(oid);
        PDU pdu = target.getVersion() == SnmpConstants.version3 ? new ScopedPDU() : new PDU();
        pdu.setType(PDU.GETNEXT);
        pdu.add(new VariableBinding(targetOID));

        while (true) {
            ResponseEvent responseEvent = snmp.getNext(pdu, target);
            PDU responsePDU = responseEvent.getResponse();
            if (responsePDU == null) break;

            VariableBinding vb = responsePDU.get(0);
            if (!vb.getOid().startsWith(targetOID)) break; // 超出范围

            results.add(vb.getVariable().toString());
            pdu.setRequestID(new Integer32(0)); // 防止请求 ID 冲突
            pdu.set(0, vb);
        }
        return results;
    }

    private static Map<String, String> snmpWalkIp(Snmp snmp, AbstractTarget target) throws Exception {
        String ipAdEntAddr = ".1.3.6.1.2.1.4.20.1.1";  // ip
        String ipAdEntIfIndexSuffix = "1.3.6.1.2.1.4.20.1.2";  // ipIndex
        String ipAdEntIfIndex = "." + ipAdEntIfIndexSuffix;  // ipIndex
        OID targetOID = new OID(ipAdEntAddr);
        OID targetOID1 = new OID(ipAdEntIfIndex);
        PDU pdu = target.getVersion() == SnmpConstants.version3 ? new ScopedPDU() : new PDU();
        pdu.setType(PDU.GETNEXT);
        pdu.add(new VariableBinding(targetOID));

        final Map<String, String> ipMap = new HashMap<>();


        while (true) {
            ResponseEvent responseEvent = snmp.getNext(pdu, target);
            PDU responsePDU = responseEvent.getResponse();
            if (responsePDU == null) {
                break;
            }

            VariableBinding vb = responsePDU.get(0);
            if (!vb.getOid().startsWith(targetOID) && !vb.getOid().startsWith(targetOID1)) {
                break; // 超出范围
            }

            if(vb.getOid().startsWith(targetOID)){
                ipMap.put(vb.getVariable().toString(), null);
            }else {
                ipMap.put(vb.getOid().toString().replace(ipAdEntIfIndexSuffix + ".", ""), vb.getVariable().toString());
            }
            pdu.setRequestID(new Integer32(0)); // 防止请求 ID 冲突
            pdu.set(0, vb);
        }
        final Map<String, String> resultMap = new HashMap<>();
        for (final Map.Entry<String, String> entry : ipMap.entrySet()) {
            resultMap.put(entry.getValue(), entry.getKey());
        }
        return resultMap;
    }

    /**
     * 执行 SNMP Walk，返回 Map (索引 -> 值)
     */
    private static Map<String, String> snmpWalkMap(Snmp snmp, AbstractTarget target, String oid) throws Exception {
        Map<String, String> resultMap = new HashMap<>();
        OID targetOID = new OID(oid);
        PDU pdu = target.getVersion() == SnmpConstants.version3 ? new ScopedPDU() : new PDU();
        pdu.setType(PDU.GETNEXT);
        pdu.add(new VariableBinding(targetOID));

        while (true) {
            ResponseEvent responseEvent = snmp.getNext(pdu, target);
            PDU responsePDU = responseEvent.getResponse();
            if (responsePDU == null) break;

            VariableBinding vb = responsePDU.get(0);
            if (!vb.getOid().startsWith(targetOID)) break; // 超出范围

            // 获取索引号
            String index = String.valueOf(vb.getOid().last());
            resultMap.put(index, vb.getVariable().toString());

            pdu.setRequestID(new Integer32(0));
            pdu.set(0, vb);
        }
        return resultMap;
    }
}

