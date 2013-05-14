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
package com.sun.xacml.cond;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeValueType;

import com.sun.xacml.EvaluationCtx;
import com.sun.xacml.attr.DoubleAttribute;
import com.sun.xacml.attr.IntegerAttribute;
import com.sun.xacml.cond.xacmlv3.EvaluationResult;
import com.thalesgroup.authzforce.xacml.schema.XACMLDatatypes;


/**
 * A class that implements all the *-subtract functions. It takes two
 * operands of the appropriate type and returns the difference of the
 * operands. If either of the operands is indeterminate, an indeterminate
 * result is returned.
 *
 * @since 1.0
 * @author Steve Hanna
 * @author Seth Proctor
 */
public class SubtractFunction extends FunctionBase
{

    /**
     * Standard identifier for the integer-subtract function.
     */
    public static final String NAME_INTEGER_SUBTRACT =
        FUNCTION_NS + "integer-subtract";
    
    /**
     * Standard identifier for the integer-subtract function.
     */
    public static final String NAME_DOUBLE_SUBTRACT =
        FUNCTION_NS + "double-subtract";

    // inernal identifiers for each of the supported functions
    private static final int ID_INTEGER_SUBTRACT = 0;
    private static final int ID_DOUBLE_SUBTRACT = 1;

    /**
     * Creates a new <code>SubtractFunction</code> object.
     *
     * @param functionName the standard XACML name of the function to be
     *                     handled by this object, including the full namespace
     *
     * @throws IllegalArgumentException if the function is unknown
     */
    public SubtractFunction(String functionName) {
        super(functionName, getId(functionName), getArgumentType(functionName),
              false, 2, getArgumentType(functionName), false);
    }

    /**
     * Private helper that returns the internal identifier used for the
     * given standard function.
     */
    private static int getId(String functionName) {
        if (functionName.equals(NAME_INTEGER_SUBTRACT))
            return ID_INTEGER_SUBTRACT;
        else if (functionName.equals(NAME_DOUBLE_SUBTRACT))
            return ID_DOUBLE_SUBTRACT;
        else
            throw new IllegalArgumentException("unknown subtract function " +
                                               functionName);
    }

    /**
     * Private helper that returns the type used for the given standard
     * function. Note that this doesn't check on the return value since the
     * method always is called after getId, so we assume that the function
     * is present.
     */
    private static String getArgumentType(String functionName) {
        if (functionName.equals(NAME_INTEGER_SUBTRACT))
            return IntegerAttribute.identifier;
        else
            return DoubleAttribute.identifier;
    }

    /**
     * Returns a <code>Set</code> containing all the function identifiers
     * supported by this class.
     *
     * @return a <code>Set</code> of <code>String</code>s
     */
    public static Set getSupportedIdentifiers() {
        Set set = new HashSet();

        set.add(NAME_INTEGER_SUBTRACT);
        set.add(NAME_DOUBLE_SUBTRACT);

        return set;
    }

    /**
     * Evaluate the function, using the specified parameters.
     *
     * @param inputs a <code>List</code> of <code>Evaluatable</code>
     *               objects representing the arguments passed to the function
     * @param context an <code>EvaluationCtx</code> so that the
     *                <code>Evaluatable</code> objects can be evaluated
     * @return an <code>EvaluationResult</code> representing the
     *         function's result
     */
    public EvaluationResult evaluate(List inputs, EvaluationCtx context) {
        
        // Evaluate the arguments
        AttributeValueType [] argValues = new AttributeValueType[inputs.size()];
        EvaluationResult result = evalArgs(inputs, context, argValues);
        if (result != null)
            return result;

        // Now that we have real values, perform the subtract operation
        // in the manner appropriate for the type of the arguments.
        switch (getId(getFunctionName())) {
        case ID_INTEGER_SUBTRACT: {
            long arg0 = Long.parseLong(argValues[0].getContent().get(0).toString());
            long arg1 = Long.parseLong(argValues[1].getContent().get(0).toString());
            long difference = arg0 - arg1;

            result = new EvaluationResult(new IntegerAttribute(difference));
            break;
        }
        case ID_DOUBLE_SUBTRACT: {
            double arg0 = Double.parseDouble(argValues[0].getContent().get(0).toString());
            double arg1 = Double.parseDouble(argValues[1].getContent().get(0).toString());
            double difference = arg0 - arg1;

            result = new EvaluationResult(new DoubleAttribute(difference));
            break;
        }
        }

        return result;
    }
}
