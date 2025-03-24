package com.vickllny;

import org.snmp4j.*;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import java.util.Map;

public class SnmpRouterDiscoveryTests {
    public static void main(String[] args) {
        // https://blog.51cto.com/u_16506291/9972457
        String targetHost = "192.168.10.1"; // 路由器 IP 地址
        String community = "public"; // SNMP 社区字符串
        int snmpPort = 161; // SNMP 端口

        try {
            // 创建 SNMP 客户端
            TransportMapping transport = new DefaultUdpTransportMapping();
            Snmp snmp = new Snmp(transport);
            transport.listen();

            // 设置目标地址和 SNMP 请求
            CommunityTarget target = new CommunityTarget();
            target.setCommunity(new OctetString(community));
            target.setVersion(SnmpConstants.version2c);
            target.setAddress(new UdpAddress(targetHost + "/" + snmpPort));
            target.setRetries(1);
            target.setTimeout(5000);

            // 获取 ARP 表信息
            Map<String, String> arpTable = snmpWalkMap(snmp, target, "1.3.6.1.2.1.4.22.1.2");  // ipNetToMediaTable OID
            //TODO 需要根据已有端口ip过滤，因为arp包含网关的ip和mac
            for (Map.Entry<String, String> entry : arpTable.entrySet()) {
                String ipAddress = entry.getKey();
                String macAddress = entry.getValue();

                // 打印 ARP 表中发现的设备
                System.out.println("IP: " + ipAddress + ", MAC: " + macAddress);

                // 可以通过查询设备的 sysDescr 来尝试识别是否为交换机
                if (isSwitch(snmp, target, macAddress)) {
                    System.out.println("Found a switch with MAC address: " + macAddress);
                }
            }

            snmp.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 使用 snmpWalkMap 查询设备的 ARP 表（ipNetToMediaTable）
    private static Map<String, String> snmpWalkMap(Snmp snmp, CommunityTarget target, String oid) throws Exception {
        Map<String, String> resultMap = new java.util.HashMap<>();
        OID targetOID = new OID(oid);
        PDU pdu = new PDU();
        pdu.setType(PDU.GETNEXT);
        pdu.add(new VariableBinding(targetOID));

        while (true) {
            ResponseEvent responseEvent = snmp.getNext(pdu, target);
            PDU responsePDU = responseEvent.getResponse();
            if (responsePDU == null) break;

            VariableBinding vb = responsePDU.get(0);
            if (!vb.getOid().startsWith(targetOID)) break;

            // 获取设备的 IP 和 MAC 地址
            final String vbString = vb.getOid().toString();
            String ipAddress = vbString.substring(oid.length() + 1);
            ipAddress = ipAddress.substring(ipAddress.indexOf(".") + 1);
            String macAddress = vb.getVariable().toString(); // 获取 MAC 地址

            resultMap.put(ipAddress, macAddress);

            pdu.setRequestID(new Integer32(0));
            pdu.set(0, vb);
        }
        return resultMap;
    }

    // 检测设备是否为交换机，尝试获取 sysDescr
    private static boolean isSwitch(Snmp snmp, CommunityTarget target, String macAddress) {
        try {
            // 使用 sysDescr OID 检查设备类型
            OID sysDescrOID = new OID("1.3.6.1.2.1.1.1.0");  // sysDescr OID
            PDU pdu = new PDU();
            pdu.setType(PDU.GET);
            pdu.add(new VariableBinding(sysDescrOID));

            // 查询设备描述信息
            ResponseEvent responseEvent = snmp.get(pdu, target);
            if (responseEvent != null && responseEvent.getResponse() != null) {
                PDU responsePDU = responseEvent.getResponse();
                VariableBinding vb = responsePDU.get(0);

                String sysDescr = vb.getVariable().toString().toLowerCase();
                if (sysDescr.contains("switch")) {
                    return true;  // 如果描述中包含 "switch" 关键字，推测为交换机
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
