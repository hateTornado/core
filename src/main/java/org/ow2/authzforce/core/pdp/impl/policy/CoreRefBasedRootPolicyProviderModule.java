/**
 * Copyright (C) 2011-2015 Thales Services SAS.
 *
 * This file is part of AuthZForce.
 *
 * AuthZForce is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * AuthZForce is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with AuthZForce. If not, see <http://www.gnu.org/licenses/>.
 */
package org.ow2.authzforce.core.pdp.impl.policy;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.IdReferenceType;

import org.ow2.authzforce.core.pdp.api.BaseStaticRootPolicyProviderModule;
import org.ow2.authzforce.core.pdp.api.CombiningAlgRegistry;
import org.ow2.authzforce.core.pdp.api.EnvironmentProperties;
import org.ow2.authzforce.core.pdp.api.ExpressionFactory;
import org.ow2.authzforce.core.pdp.api.IPolicyEvaluator;
import org.ow2.authzforce.core.pdp.api.IndeterminateEvaluationException;
import org.ow2.authzforce.core.pdp.api.JaxbXACMLUtils.XACMLParserFactory;
import org.ow2.authzforce.core.pdp.api.RefPolicyProviderModule;
import org.ow2.authzforce.core.pdp.api.RootPolicyProviderModule;
import org.ow2.authzforce.core.pdp.api.VersionPatterns;
import org.ow2.authzforce.core.xmlns.pdp.StaticRefBasedRootPolicyProvider;
import org.ow2.authzforce.xmlns.pdp.ext.AbstractPolicyProvider;

/**
 * This Root policy provider module retrieves the root policy from a {@link RefPolicyProviderModule} statically (once and for all), based on a XACML
 * PolicySetIdReference.
 */
public class CoreRefBasedRootPolicyProviderModule extends BaseStaticRootPolicyProviderModule
{
	private static final IllegalArgumentException ILLEGAL_XML_CONF_ARG_EXCEPTION = new IllegalArgumentException("Undefined XML/JAXB configuration");
	private static final IllegalArgumentException ILLEGAL_XACML_POLICY_REF_ARG_EXCEPTION = new IllegalArgumentException("Undefined XACML PolicySetIdReference");

	/**
	 * Module factory
	 * 
	 */
	public static class Factory extends RootPolicyProviderModule.Factory<StaticRefBasedRootPolicyProvider>
	{

		@Override
		public Class<StaticRefBasedRootPolicyProvider> getJaxbClass()
		{
			return StaticRefBasedRootPolicyProvider.class;
		}

		@Override
		public <REF_POLICY_PROVIDER_CONF extends AbstractPolicyProvider> RootPolicyProviderModule getInstance(StaticRefBasedRootPolicyProvider jaxbConf,
				XACMLParserFactory xacmlParserFactory, ExpressionFactory expressionFactory, CombiningAlgRegistry combiningAlgRegistry,
				REF_POLICY_PROVIDER_CONF jaxbRefPolicyProviderConf, RefPolicyProviderModule.Factory<REF_POLICY_PROVIDER_CONF> refPolicyProviderModuleFactory,
				int maxPolicySetRefDepth, EnvironmentProperties environmentProperties)
		{
			if (jaxbConf == null)
			{
				throw ILLEGAL_XML_CONF_ARG_EXCEPTION;
			}

			return new CoreRefBasedRootPolicyProviderModule(jaxbConf.getPolicyRef(), expressionFactory, combiningAlgRegistry, xacmlParserFactory,
					jaxbRefPolicyProviderConf, refPolicyProviderModuleFactory, maxPolicySetRefDepth, environmentProperties);
		}
	}

	private final IPolicyEvaluator rootPolicy;

	/**
	 * Creates instance with the root PolicySet retrieved from the refPolicyprovider once and for all
	 * 
	 * @param policyRef
	 *            Policy(Set)Id reference to be resolved by the refPolicyProvider module
	 * @param combiningAlgRegistry
	 *            registry of policy/rule combining algorithms
	 * @param expressionFactory
	 *            Expression factory for parsing Expressions used in the policy(set)
	 * @param jaxbRefPolicyProviderConf
	 *            XML/JAXB configuration of RefPolicyProvider module used for resolving Policy(Set)(Id)References in {@code jaxbPolicySet}; may be null if
	 *            support of PolicyReferences is disabled or this RootPolicyProvider module already supports these.
	 * @param maxPolicySetRefDepth
	 *            maximum depth of PolicySet reference chaining via PolicySetIdReference that is allowed in RefPolicyProvider derived from
	 *            {@code jaxbRefPolicyProviderConf}: PolicySet1 -> PolicySet2 -> ...; iff {@code jaxbRefPolicyProviderConf == null}, this parameter is ignored.
	 * @param xacmlParserFactory
	 *            XACML Parser factory; may be null if {@code jaxbRefPolicyProviderConf} as it is meant to be used by the RefPolicyProvider module
	 * @param refPolicyProviderModFactory
	 *            refPolicyProvider module factory for creating a module instance from configuration defined by {@code jaxbRefPolicyProviderConf}
	 * @param environmentProperties
	 *            PDP configuration environment properties
	 * @throws IllegalArgumentException
	 *             if {@code policySetRef} is null/invalid, or if
	 *             {@code jaxbRefPolicyProviderConf != null && (expressionFactory == null || combiningAlgRegistry == null || xacmlParserFactory == null)} or no
	 *             PolicySet matching {@code policySetRef} could be resolved by the refPolicyProvider
	 */
	public <CONF extends AbstractPolicyProvider> CoreRefBasedRootPolicyProviderModule(IdReferenceType policyRef, ExpressionFactory expressionFactory,
			CombiningAlgRegistry combiningAlgRegistry, XACMLParserFactory xacmlParserFactory, CONF jaxbRefPolicyProviderConf,
			RefPolicyProviderModule.Factory<CONF> refPolicyProviderModFactory, int maxPolicySetRefDepth, EnvironmentProperties environmentProperties)
			throws IllegalArgumentException
	{
		super(expressionFactory, combiningAlgRegistry, xacmlParserFactory, jaxbRefPolicyProviderConf, refPolicyProviderModFactory, maxPolicySetRefDepth,
				environmentProperties);
		if (policyRef == null)
		{
			throw ILLEGAL_XACML_POLICY_REF_ARG_EXCEPTION;
		}

		final String policySetId = policyRef.getValue();
		final VersionPatterns versionPatterns = new VersionPatterns(policyRef.getVersion(), policyRef.getEarliestVersion(), policyRef.getLatestVersion());
		try
		{
			rootPolicy = refPolicyProvider.get(IPolicyEvaluator.class, policySetId, versionPatterns, null);
		} catch (IndeterminateEvaluationException e)
		{
			throw new IllegalArgumentException("Error resolving/instantiating the root policy matching PolicySetIdReference: PolicySetId = " + policySetId
					+ "; " + versionPatterns, e);
		}

		if (rootPolicy == null)
		{
			throw new IllegalArgumentException("No policy found by the refPolicyProvider for the specified PolicySetIdReference: PolicySetId = " + policySetId
					+ "; " + versionPatterns);
		}
	}

	@Override
	public IPolicyEvaluator getRootPolicy()
	{
		return rootPolicy;
	}
}