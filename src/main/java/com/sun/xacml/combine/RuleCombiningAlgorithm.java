/**
 * Copyright (C) 2011-2013 Thales Services - ThereSIS - All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.sun.xacml.combine;

import java.net.URI;
import java.util.List;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.CombinerParametersType;

import com.sun.xacml.EvaluationCtx;
import com.sun.xacml.ctx.Result;
import com.thalesgroup.authzforce.audit.annotations.Audit;


/**
 * The base type for all Rule combining algorithms.
 *
 * @since 1.0
 * @author Seth Proctor
 * @author Marco Barreno
 */
public abstract class RuleCombiningAlgorithm extends CombiningAlgorithm
{

    /**
     * Constructor that takes the algorithm's identifier.
     *
     * @param identifier the algorithm's identifier
     */
    public RuleCombiningAlgorithm(URI identifier) {
        super(identifier);
    }
    
    /**
     * Combines the rules based on the context to produce some unified
     * result. This is the one function of a combining algorithm.
     *
     * @param context the representation of the request
     * @param parameters a (possibly empty) non-null <code>List</code> of
     *                   <code>CombinerParameter<code>s
     * @param ruleElements a <code>List</code> of <code>CombinerElement<code>s
     *
     * @return a single unified result based on the combining logic
     */
    @Audit(type = Audit.Type.RULE)
    public abstract Result combine(EvaluationCtx context, CombinerParametersType parameters,
                                   List ruleElements);

}
