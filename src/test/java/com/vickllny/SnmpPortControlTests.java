package com.vickllny;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import java.io.IOException;

public class SnmpPortControlTests {
    public static void main(String[] args) {
        String targetHost = "192.168.10.1"; // 路由器 IP 地址
        String community = "private"; // SNMP 社区字符串（有权限修改接口状态）
        int snmpPort = 161; // SNMP 端口
        int interfaceIndex = 2; // 接口索引（例如：2）

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

            // 启用端口
//            setPortStatus(snmp, target, interfaceIndex, true);

            // 关闭端口
             setPortStatus(snmp, target, interfaceIndex, false);

            snmp.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 设置端口状态
    public static void setPortStatus(Snmp snmp, CommunityTarget target, int interfaceIndex, boolean enable) throws IOException {
        // 设置 OID：1.3.6.1.2.1.2.2.1.7. 接口索引是动态的，可以根据实际情况变化
        OID ifAdminStatusOID = new OID("1.3.6.1.2.1.2.2.1.7." + interfaceIndex);

        // 设置 PDU 请求类型为 SET
        PDU pdu = new PDU();
        pdu.setType(PDU.SET);

        // 设置接口状态，1 表示启用，2 表示禁用
        int status = enable ? 1 : 2; // 1 = up, 2 = down
        pdu.add(new VariableBinding(ifAdminStatusOID, new Integer32(status)));

        // 发送 SNMP 请求
        ResponseEvent responseEvent = snmp.send(pdu, target);
        if (responseEvent != null) {
            PDU responsePDU = responseEvent.getResponse();
            if (responsePDU != null) {
                System.out.println("Response: " + responsePDU.getVariableBindings());
                if (status == 1) {
                    System.out.println("Port " + interfaceIndex + " is enabled.");
                } else {
                    System.out.println("Port " + interfaceIndex + " is disabled.");
                }
            } else {
                System.out.println("SNMP response is null.");
            }
        } else {
            System.out.println("SNMP request failed.");
        }
    }
}