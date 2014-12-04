package com.networknt.light.rule.form;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import com.networknt.light.rule.Rule;
import com.networknt.light.util.ServiceLocator;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.*;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by steve on 9/4/2014.
 *
 * Is there a way to verify that the memoryImage is in sync with db?
 * In that case, we don't need to reload from db every time this rule is executed.
 * What we can do is to load all forms in the beginning when server starts, and make
 * sure all the form updates are gone through these set of rules.
 *
 */
public class GetAllFormRule extends AbstractFormRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        if(payload == null) {
            inputMap.put("error", "Login is required");
            inputMap.put("responseCode", 401);
            return false;
        }
        Map<String, Object> user = (Map<String, Object>) payload.get("user");
        List roles = (List)user.get("roles");
        if(roles.contains("owner") || roles.contains("admin") || roles.contains("formAdmin")) {
            String host = (String) user.get("host");
            String hostForms = getAllForm(host);
            if(hostForms != null) {
                inputMap.put("result", hostForms);
                return true;
            } else {
                inputMap.put("result", "No form can be found.");
                inputMap.put("responseCode", 404);
                return false;
            }
        } else {
            inputMap.put("result", "Permission denied");
            inputMap.put("responseCode", 401);
            return false;
        }
    }
}