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


/**
 * This is the standard Ordered Deny Overrides rule combining algorithm. It
 * allows a single evaluation of Deny to take precedence over any number
 * of permit, not applicable or indeterminate results. Note that this uses
 * the regular Deny Overrides implementation since it is also orderd.
 *
 * @since 1.1
 * @author seth proctor
 */
public class OrderedDenyOverridesRuleAlg extends DenyOverridesRuleAlg
{

    /**
     * The standard URN used to identify this algorithm
     */
    public static final String algId =
        "urn:oasis:names:tc:xacml:1.1:rule-combining-algorithm:" +
        "ordered-deny-overrides";

    // a URI form of the identifier
    private static final URI identifierURI = URI.create(algId);

    /**
     * Standard constructor.
     */
    public OrderedDenyOverridesRuleAlg() {
        super(identifierURI);
    }

}
