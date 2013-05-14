/**
 * Copyright (C) 2012-2013 Thales Services - ThereSIS - All rights reserved.
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
import java.util.Iterator;
import java.util.List;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.CombinerParametersType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.DecisionType;

import com.sun.xacml.EvaluationCtx;
import com.sun.xacml.MatchResult;
import com.sun.xacml.ctx.Result;
import com.sun.xacml.xacmlv3.Policy;


/**
 * This is the standard First Applicable policy combining algorithm. It looks
 * through the set of policies, finds the first one that applies, and returns
 * that evaluation result.
 *
 * @since 1.0
 * @author Seth Proctor
 */
public class FirstApplicablePolicyAlg extends PolicyCombiningAlgorithm
{
    
    /**
     * The standard URN used to identify this algorithm
     */
    public static final String algId =
        "urn:oasis:names:tc:xacml:1.0:policy-combining-algorithm:" +
        "first-applicable";

    // a URI form of the identifier
    private static final URI identifierURI = URI.create(algId);

    /**
     * Standard constructor.
     */
    public FirstApplicablePolicyAlg() {
        super(identifierURI);
    }

    /**
     * Applies the combining rule to the set of policies based on the
     * evaluation context.
     *
     * @param context the context from the request
     * @param parameters a (possibly empty) non-null <code>List</code> of
     *                   <code>CombinerParameter<code>s
     * @param policyElements the policies to combine
     *
     * @return the result of running the combining algorithm
     */
    public Result combine(EvaluationCtx context, CombinerParametersType parameters,
                          List policyElements) {
        Iterator it = policyElements.iterator();
        
        while (it.hasNext()) {
            Policy policy = ((Policy)(it.next()));

            // make sure that the policy matches the context
            MatchResult match = policy.match(context);

            if (match.getResult() == MatchResult.INDETERMINATE)
                return new Result(DecisionType.INDETERMINATE,
                                  match.getStatus(),
                                  context.getResourceId().encode());

            if (match.getResult() == MatchResult.MATCH) {
                // evaluate the policy
                Result result = policy.evaluate(context);
                int effect = result.getDecision().ordinal();
                
                // in the case of PERMIT, DENY, or INDETERMINATE, we always
                // just return that result, so only on a rule that doesn't
                // apply do we keep going...
                if (effect != Result.DECISION_NOT_APPLICABLE)
                    return result;
            }
        }

        // if we got here, then none of the rules applied
        return new Result(DecisionType.NOT_APPLICABLE, context.getResourceId().encode());
    }

}
