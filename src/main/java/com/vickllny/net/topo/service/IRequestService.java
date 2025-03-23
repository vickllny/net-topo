package com.vickllny.net.topo.service;

import com.vickllny.net.topo.protocol.SNMPProtocol;

public interface IRequestService {

    Object request(SNMPProtocol snmp);

}
