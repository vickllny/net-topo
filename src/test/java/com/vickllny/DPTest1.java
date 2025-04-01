//package com.vickllny;
//
//import com.vickllny.net.topo.constants.OIDS;
//import com.vickllny.net.topo.protocol.SNMPV3;
//import org.snmp4j.*;
//import org.snmp4j.event.ResponseEvent;
//import org.snmp4j.mp.MPv3;
//import org.snmp4j.mp.SnmpConstants;
//import org.snmp4j.security.*;
//import org.snmp4j.smi.*;
//import org.snmp4j.transport.DefaultUdpTransportMapping;
//import org.snmp4j.util.TableEvent;
//
//import java.io.IOException;
//import java.util.*;
//
//public class DPTest1 {
//
//    public class SNMPUtils {
//        private Snmp snmp;
//        private String ip;
//        private int port;
//        private int version;
//
//        public SNMPUtils(String ip, int port, int version) {
//            this.ip = ip;
//            this.port = port;
//            this.version = version;
//        }
//
//        public void start() throws IOException {
//            TransportMapping transport = new DefaultUdpTransportMapping();
//            snmp = new Snmp(transport);
//            transport.listen();
//        }
//
//        public void stop() throws IOException {
//            if (snmp != null) {
//                snmp.close();
//            }
//        }
//
//        public String getAsString(OID oid) throws IOException {
//            ScopedPDU pdu = new ScopedPDU();
//            pdu.add(new VariableBinding(oid));
//            pdu.setType(PDU.GET);
//
//            ResponseEvent event = snmp.send(pdu, getTarget());
//            if (event != null && event.getResponse() != null) {
//                return event.getResponse().get(0).getVariable().toString();
//            }
//            return null;
//        }
//
//        public TableEvent getTable(OID[] oids) throws IOException {
//            ScopedPDU pdu = new ScopedPDU();
//            for (OID oid : oids) {
//                pdu.add(new VariableBinding(oid));
//            }
//            pdu.setType(PDU.GETBULK);
//            pdu.setMaxRepetitions(50);
//
//            return snmp.getTable(pdu, getTarget());
//        }
//
//        private Target getTarget() {
////            Address targetAddress = GenericAddress.parse(ip);
////            CommunityTarget target = new CommunityTarget();
////            target.setCommunity(new OctetString(community));
////            target.setAddress(targetAddress);
////            target.setRetries(2);
////            target.setTimeout(1500);
////            target.setVersion(version);
////            return target;
//
//
//
//            final SNMPV3 snmpv3 = new SNMPV3();
//            snmpv3.setIp(ip);
//            snmpv3.setPort(port);
//            snmpv3.setOid(OIDS.SYSTEM_NAME);
//            snmpv3.setUsername("zouq");
//            snmpv3.setLevel(SecurityLevel.authPriv);
//            snmpv3.setAuthProtocol(new AuthMD5());
//            snmpv3.setPrivacyProtocol(new PrivDES());
//            snmpv3.setAuthPassword("qaq123456-");
//            snmpv3.setPrivacyPassword("qaq123456-");
//
//            USM usm = new USM(SecurityProtocols.getInstance(), new OctetString(MPv3.createLocalEngineID()), 0);
//            SecurityModels.getInstance().addSecurityModel(usm);
//
//            // 添加用户
//            snmp.getUSM().addUser(
//                    new OctetString(snmpv3.getUsername()),
//                    new UsmUser(
//                            new OctetString(snmpv3.getUsername()),
//                            snmpv3.getAuthProtocol().getID(), new OctetString(snmpv3.getAuthPassword()),
//                            snmpv3.getPrivacyProtocol().getID(), new OctetString(snmpv3.getPrivacyPassword())
//                    )
//            );
//
//            return snmpv3.target();
//        }
//    }
//
//
//    public class LLDPTopologyDiscovery {
//        // LLDP-MIB OIDs
//        private final OID LLDP_REMOTE_CHASSIS_ID = new OID("1.0.8802.1.1.2.1.4.1.1.5");
//        private final OID LLDP_REMOTE_PORT_ID = new OID("1.0.8802.1.1.2.1.4.1.1.7");
//        private final OID LLDP_LOCAL_PORT = new OID("1.0.8802.1.1.2.1.4.1.1.2");
//
//        private String startDeviceIP;
//        private String community;
//        private Set<String> discoveredDevices = new HashSet<>();
//        private Map<String, List<String>> topology = new HashMap<>();
//
//        public LLDPTopologyDiscovery(String startDeviceIP, String community) {
//            this.startDeviceIP = startDeviceIP;
//            this.community = community;
//        }
//
//        public void discoverTopology() {
//            discoveredDevices.clear();
//            topology.clear();
//            discoverDevice(startDeviceIP);
//        }
//
//        private void discoverDevice(String deviceIP) {
//            if (discoveredDevices.contains(deviceIP)) {
//                return;
//            }
//            discoveredDevices.add(deviceIP);
//
//            try {
//                SNMPUtils snmp = new SNMPUtils("192.168.10.1", 161, SnmpConstants.version3);
//                snmp.start();
//
//                // Get LLDP neighbors
//                OID[] oids = {LLDP_LOCAL_PORT, LLDP_REMOTE_CHASSIS_ID, LLDP_REMOTE_PORT_ID};
//                ResponseEvent event = snmp.getTable(oids);
//
//                if (event != null && event.getResponse() != null) {
//                    List<String> neighbors = new ArrayList<>();
//
//                    for (VariableBinding vb : event.getResponse().getVariableBindings()) {
//                        if (vb.getOid().startsWith(LLDP_REMOTE_CHASSIS_ID)) {
//                            String remoteChassisId = vb.getVariable().toString();
//                            // 这里可以添加逻辑将Chassis ID解析为IP地址
//                            // 简化处理，直接使用Chassis ID
//                            neighbors.add(remoteChassisId);
//
//                            // 递归发现新设备
//                            if (!discoveredDevices.contains(remoteChassisId)) {
//                                discoverDevice(remoteChassisId);
//                            }
//                        }
//                    }
//
//                    topology.put(deviceIP, neighbors);
//                }
//
//                snmp.stop();
//            } catch (IOException e) {
//                System.err.println("Error discovering device " + deviceIP + ": " + e.getMessage());
//            }
//        }
//
//        public void printTopology() {
//            System.out.println("Network Topology:");
//            for (Map.Entry<String, List<String>> entry : topology.entrySet()) {
//                System.out.print(entry.getKey() + " -> ");
//                if (entry.getValue().isEmpty()) {
//                    System.out.println("No neighbors");
//                } else {
//                    System.out.println(String.join(", ", entry.getValue()));
//                }
//            }
//        }
//    }
//}
