package com.ilimi.graph.dac.mgr;

import com.ilimi.common.dto.Request;
import com.ilimi.common.dto.Response;

public interface IGraphDACSearchMgr {

	Response getNodeById(Request request);

	Response getNodeByUniqueId(Request request);

	Response getNodesByUniqueIds(Request request);

	Response getNodesByProperty(Request request);

	Response getNodeProperty(Request request);

	Response getAllRelations(Request request);

	Response getAllNodes(Request request);

	Response getRelation(Request request);

	Response getRelationProperty(Request request);

	Response checkCyclicLoop(Request request);
    
	Response executeQuery(Request request);
	
	Response executeQueryForProps(Request request);

	Response searchNodes(Request request);

	Response getNodesCount(Request request);

	Response traverse(Request request);
    
	Response traverseSubGraph(Request request);
    
	Response getSubGraph(Request request);
}
