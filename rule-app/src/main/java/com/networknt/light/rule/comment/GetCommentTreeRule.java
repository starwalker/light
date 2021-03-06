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

package com.networknt.light.rule.comment;

import com.networknt.light.rule.Rule;

import java.util.Map;

/**
 * Created by steve on 03/12/14.
 *
 * AccessLevel A
 *
 * everyone can have read only access to all comments
 *
 */
public class GetCommentTreeRule extends AbstractCommentRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>)inputMap.get("data");
        String comments = getCommentTree(data);
        if(comments != null) {
            inputMap.put("result", comments);
            return true;
        } else {
            inputMap.put("result", "No comment can be found");
            inputMap.put("responseCode", 404);
            return false;
        }
    }
}
