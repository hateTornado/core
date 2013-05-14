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
package com.sun.xacml.cond.cluster;

import com.sun.xacml.cond.HigherOrderFunction;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 * Clusters all the functions supported by <code>HigherOrderFunction</code>.
 *
 * @since 1.2
 * @author Seth Proctor
 */
public class HigherOrderFunctionCluster implements FunctionCluster
{
	/**
	 * Logger used for all classes
	 */
	private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger
			.getLogger(HigherOrderFunctionCluster.class);
	
    public Set getSupportedFunctions() {
        Set set = new HashSet();
        Iterator it = HigherOrderFunction.getSupportedIdentifiers().iterator();

        LOGGER.debug("Initialize Higher Order function");
        while (it.hasNext())
            set.add(new HigherOrderFunction((String)(it.next())));

        return set;
    }

}
