/*
 * Copyright 2015 Network New Technologies Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.networknt.light.rule.product;

import com.networknt.light.rule.Rule;
import com.networknt.light.server.DbService;
import com.networknt.light.util.ServiceLocator;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by steve on 26/04/15.
 *
 * AccessLevel R [user]
 */
public class DownProductRule extends AbstractProductRule implements Rule {
    static final Logger logger = LoggerFactory.getLogger(DownProductRule.class);

    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>)objects[0];
        Map<String, Object> data = (Map<String, Object>)inputMap.get("data");
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        Map<String,Object> user = (Map<String, Object>)payload.get("user");
        String rid = (String)data.get("@rid");
        String error = null;
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            OrientVertex product = (OrientVertex) DbService.getVertexByRid(graph, rid);
            OrientVertex voteUser = (OrientVertex)graph.getVertexByKey("User.userId", user.get("userId"));
            if(product == null) {
                error = "@rid " + rid + " cannot be found";
                inputMap.put("responseCode", 404);
            } else {
                boolean voted = false;
                for (Edge edge : voteUser.getEdges(product, Direction.OUT, "DownVote")) {
                    if(edge.getVertex(Direction.IN).equals(product)) voted = true;
                }
                if(voted) {
                    error = "You have down voted the product already";
                    inputMap.put("responseCode", 400);
                } else {
                    Map eventMap = getEventMap(inputMap);
                    Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
                    inputMap.put("eventMap", eventMap);
                    eventData.put("productId", product.getProperty("productId"));
                    eventData.put("updateUserId", user.get("userId"));
                }
            }
        } catch (Exception e) {
            logger.error("Exception:", e);
            throw e;
        } finally {
            graph.shutdown();
        }
        if(error != null) {
            inputMap.put("result", error);
            return false;
        } else {
            return true;
        }
    }
}
