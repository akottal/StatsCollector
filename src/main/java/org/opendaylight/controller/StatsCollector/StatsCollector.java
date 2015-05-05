/*
 * Copyright (C) 2014 SDN Hub

 Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3.
 You may not use this file except in compliance with this License.
 You may obtain a copy of the License at

    http://www.gnu.org/licenses/gpl-3.0.txt

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 implied.

 *
 */

package org.opendaylight.controller;

import org.opendaylight.controller.sal.core.Node;
import org.opendaylight.controller.sal.core.NodeConnector;
import org.opendaylight.controller.sal.match.MatchType;
import org.opendaylight.controller.sal.reader.FlowOnNode;
import org.opendaylight.controller.sal.reader.NodeConnectorStatistics;
import org.opendaylight.controller.sal.reader.IReadService;
import org.opendaylight.controller.sal.utils.ServiceHelper;
import org.opendaylight.controller.statisticsmanager.IStatisticsManager;
import org.opendaylight.controller.switchmanager.ISwitchManager;
import org.opendaylight.controller.topologymanager.ITopologyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class StatsCollector {
    private static final Logger logger = LoggerFactory
            .getLogger(StatsCollector.class);
//    private ISwitchManager switchManager = null;
//    private IReadService readStats = null;
//    private ITopologyManager topologyManager = null;
    private Set<NodeConnector> allConnectors = null;

    void init() {
        logger.info("INIT called!");
    }

    void destroy() {
        logger.info("DESTROY called!");
    }

    void start() {
        logger.info("START called!");
        getFlowStatistics();
    }

    void stop() {
        logger.info("STOP called!");
    }

    void getFlowStatistics() {
        String containerName = "default";
        IStatisticsManager statsManager = (IStatisticsManager) ServiceHelper
                .getInstance(IStatisticsManager.class, containerName, this);

        ISwitchManager switchManager = (ISwitchManager) ServiceHelper
                .getInstance(ISwitchManager.class, containerName, this);

	ITopologyManager topologyManager = (ITopologyManager) ServiceHelper
                .getInstance(ITopologyManager.class, containerName, this);

	IReadService readStats = (IReadService) ServiceHelper
		.getInstance(IReadService.class, containerName, this);

        for (Node node : switchManager.getNodes()) {
            System.out.println("\n\nNode: " + node);
            for (FlowOnNode flow : statsManager.getFlows(node)) {
                System.out.println(" DST: "
                        + flow.getFlow().getMatch().getField(MatchType.NW_DST)
                        + " Bytes: " + flow.getByteCount());
            }

		/* New Code */
		allConnectors = switchManager.getNodeConnectors(node);
		for(NodeConnector connector : allConnectors) {
			System.out.println("Node Connector: " + connector.toString());
			if(topologyManager.getHostsAttachedToNodeConnector(connector) != null)
				System.out.println("getHostsAttachedtoNodeConnector: " + topologyManager.getHostsAttachedToNodeConnector(connector).toString());
			if(statsManager.getNodeConnectorStatistics(connector) != null)
				System.out.println("Get Node Connector Statistics " + statsManager.getNodeConnectorStatistics(connector).toString());
			System.out.println("Transmit Rate for this connector: " + readStats.getTransmitRate(connector) + "bps");
			System.out.println("\n\n\n");
		}
        }

	if(topologyManager.getEdges() != null)
		System.out.println("getEdges: " + topologyManager.getEdges().toString());
	if(topologyManager.getNodeConnectorWithHost() != null)
		System.out.println("getNodeConnectorWithHost: " + topologyManager.getNodeConnectorWithHost().toString());
	if(topologyManager.getNodeEdges() != null)
		System.out.println("getNodeEdges: " + topologyManager.getNodeEdges().toString());
	if(topologyManager.getNodesWithNodeConnectorHost() != null)
		System.out.println("getNodesWithNodeConnectorHost: " + topologyManager.getNodesWithNodeConnectorHost().toString());
    }
}
