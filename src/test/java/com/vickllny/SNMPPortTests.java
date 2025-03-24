package com.vickllny;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class SNMPPortTests {
    public static void main(String[] args) {
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

            // 获取接口信息
            getInterfacesInfo(snmp, target);

            snmp.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 获取接口的 MAC 地址、IP 地址、名称和状态
    public static void getInterfacesInfo(Snmp snmp, CommunityTarget target) throws IOException {
        String ifDescrOid = "1.3.6.1.2.1.2.2.1.2"; // 获取接口名称
        String ifPhysAddressOid = "1.3.6.1.2.1.2.2.1.6"; // 获取接口 MAC 地址
        String ifOperStatusOid = "1.3.6.1.2.1.2.2.1.8"; // 获取接口状态
        String ipAdEntAddrOid = ".1.3.6.1.2.1.4.20.1.1"; // 获取接口 IP 地址

        // 获取接口信息
        Map<String, InterfaceInfo> interfaceMap = new HashMap<>();
        collectInterfaceInfo(snmp, target, ifDescrOid, interfaceMap, "name");
        collectInterfaceInfo(snmp, target, ifPhysAddressOid, interfaceMap, "mac");
        collectInterfaceInfo(snmp, target, ifOperStatusOid, interfaceMap, "status");
        collectInterfaceInfo(snmp, target, ipAdEntAddrOid, interfaceMap, "ip");

        // 打印所有接口的信息，按索引号分组
        printInterfaceInfo(interfaceMap);
    }

    // 获取接口信息的通用方法
    public static void collectInterfaceInfo(Snmp snmp, CommunityTarget target, String oid,
                                            Map<String, InterfaceInfo> interfaceMap, String type) throws IOException {
        OID currentOid = new OID(oid);
        boolean isDone = false;

        while (!isDone) {
            PDU pdu = new PDU();
            pdu.add(new VariableBinding(currentOid));
            pdu.setType(PDU.GETNEXT);

            ResponseEvent responseEvent = snmp.send(pdu, target);
            if (responseEvent != null) {
                PDU response = responseEvent.getResponse();
                if (response != null) {
                    Vector<? extends VariableBinding> varBindings = response.getVariableBindings();
                    if (varBindings != null && !varBindings.isEmpty()) {
                        for (VariableBinding vb : varBindings) {
                            OID receivedOid = vb.getOid();
                            String value = vb.getVariable().toString();

                            if (receivedOid.toString().startsWith(oid)) {
                                String index = String.valueOf(receivedOid.get(oid.split("\\.").length));
                                InterfaceInfo interfaceInfo = interfaceMap.getOrDefault(index, new InterfaceInfo(index));

                                // 根据不同类型更新接口信息
                                if (type.equals("name")) {
                                    interfaceInfo.setName(value);
                                } else if (type.equals("mac")) {
                                    interfaceInfo.setMac(value);
                                } else if (type.equals("status")) {
                                    interfaceInfo.setStatus(value);
                                } else if (type.equals("ip")) {
                                    interfaceInfo.setIp(value);
                                }
                                interfaceMap.put(index, interfaceInfo);
                                currentOid = receivedOid; // 更新 OID，用于获取下一个接口信息
                            } else {
                                isDone = true; // OID 越界，结束遍历
                            }
                        }
                    }
                }
            }
        }
    }

    // 打印所有接口的信息
    public static void printInterfaceInfo(Map<String, InterfaceInfo> interfaceMap) {
        System.out.println("Interfaces Information (Grouped by ifIndex):");
        for (Map.Entry<String, InterfaceInfo> entry : interfaceMap.entrySet()) {
            InterfaceInfo info = entry.getValue();
            System.out.println("Interface Index: " + info.getIndex());
            System.out.println("  Name: " + info.getName());
            System.out.println("  MAC Address: " + info.getMac());
            System.out.println("  IP Address: " + info.getIp());
            System.out.println("  Status: " + info.getStatus());
            System.out.println("---------------");
        }
    }
}

// 接口信息类，包含名称、MAC 地址、IP 地址和状态
class InterfaceInfo {
    private String index;
    private String name;
    private String mac;
    private String ip;
    private String status;

    public InterfaceInfo(String index) {
        this.index = index;
    }

    public String getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
