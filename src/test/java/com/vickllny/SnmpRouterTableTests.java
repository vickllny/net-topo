package com.vickllny;

import org.snmp4j.*;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SnmpRouterTableTests {

    public static void main(String[] args) {
        String routerIp = "192.168.10.1"; // 你的路由器 IP
        String community = "public"; // SNMP 只读社区
        int snmpPort = 161; // SNMP 端口

        try {
            // 创建 SNMP 客户端
            TransportMapping transport = new DefaultUdpTransportMapping();
            Snmp snmp = new Snmp(transport);
            transport.listen();

            // 设置目标地址
            CommunityTarget target = new CommunityTarget();
            target.setCommunity(new OctetString(community));
            target.setVersion(SnmpConstants.version2c);
            target.setAddress(new UdpAddress(routerIp + "/" + snmpPort));
            target.setRetries(2);
            target.setTimeout(5000);

            // 获取路由表
            Map<String, Map<String, String>> routingTable = getRoutingTable(snmp, target);

            // 打印路由表
            System.out.println("Router Routing Table:");
            for (String dest : routingTable.keySet()) {
                System.out.println("Destination: " + dest);
                for (Map.Entry<String, String> entry : routingTable.get(dest).entrySet()) {
                    System.out.println("  " + entry.getKey() + ": " + entry.getValue());
                }
                System.out.println("--------------------------------");
            }

            snmp.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 获取路由表
    private static Map<String, Map<String, String>> getRoutingTable(Snmp snmp, CommunityTarget target) throws IOException {
        Map<String, Map<String, String>> routingTable = new HashMap<>();

        // 需要查询的 OID
        String[] oids = {
                "1.3.6.1.2.1.4.21.1.1",  // ipRouteDest
                "1.3.6.1.2.1.4.21.1.7",  // ipRouteNextHop
                "1.3.6.1.2.1.4.21.1.8",  // ipRouteType
                "1.3.6.1.2.1.4.21.1.11"  // ipRouteMask
        };

        for (String oid : oids) {
            Map<String, String> result = snmpWalk(snmp, target, oid);
            for (Map.Entry<String, String> entry : result.entrySet()) {
                String index = entry.getKey();
                String value = entry.getValue();

                routingTable.putIfAbsent(index, new HashMap<>());
                routingTable.get(index).put(oid, value);
            }
        }

        return routingTable;
    }

    // 使用 SNMP Walk 获取路由表的某个字段
    private static Map<String, String> snmpWalk(Snmp snmp, CommunityTarget target, String oid) throws IOException {
        Map<String, String> resultMap = new HashMap<>();
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

            // 提取索引（即目的 IP）
            String index = vb.getOid().last() + "";
            resultMap.put(index, vb.getVariable().toString());

            pdu.setRequestID(new Integer32(0));
            pdu.set(0, vb);
        }
        return resultMap;
    }
}
