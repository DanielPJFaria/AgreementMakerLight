/******************************************************************************
 * Copyright 2013-2020 LASIGE                                                  *
 *                                                                             *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may     *
 * not use this file except in compliance with the License. You may obtain a   *
 * copy of the License at http://www.apache.org/licenses/LICENSE-2.0           *
 *                                                                             *
 * Unless required by applicable law or agreed to in writing, software         *
 * distributed under the License is distributed on an "AS IS" BASIS,           *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.    *
 * See the License for the specific language governing permissions and         *
 * limitations under the License.                                              *
 *                                                                             *
 *******************************************************************************
 * Matcher based on association rules. Finds complex (1:n) relationships       *
 * between classes and AttributeTypeRestrictions - Class by Attribute Value    *
 * (CAV) patterns.                                                             *
 * @authors Beatriz Lima, Daniel Faria                                         *
 ******************************************************************************/
package aml.match.association;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLDatatype;
import aml.AML;
import aml.alignment.rdf.AttributeTypeRestriction;
import aml.alignment.rdf.ClassId;
import aml.alignment.rdf.Datatype;
import aml.alignment.rdf.PropertyId;
import aml.ontology.Ontology;
import aml.ontology.ValueMap;
import aml.ontology.semantics.EntityMap;

public class CAVARMatcher extends aml.match.association.AbstractAssociationRuleMatcher
{

	// Attributes

	//Constructor
	public CAVARMatcher() 
	{
		super();
	}

	//Protected methods
	/*
	 * Populates EntitySupport and MappingSupport tables
	 * @param o1: source ontology
	 * @param o2: target ontology
	 */
	protected void computeSupport(Ontology o1, Ontology o2) 
	{
		// Get entity map of relations
		Set<String> sharedInstances = new HashSet<String>(getSharedInstances(o1,o2));
		EntityMap rels = AML.getInstance().getEntityMap();
		ValueMap srcValueMap = o1.getValueMap();
		ValueMap tgtValueMap = o2.getValueMap();
		Set<String> srcProperties = null;
		Set<String> tgtProperties = null;
		Set<String> equivIndv = new HashSet<String>();


		for(String si : sharedInstances) 
		{
			// Find all classes of that instance
			Set<String> cSet = rels.getIndividualClasses(si);

			// Switch to list since we need indexes 
			List<String> i1Classes = new ArrayList<String>(cSet);
			// If empty list of classes, move on to next instance
			int len = i1Classes.size();
			if(len < 1) {continue;}

			// If map contains any properties linking to that instance, get them
			if (srcValueMap.getProperties(si) != null) 
				srcProperties = new HashSet<String>(srcValueMap.getProperties(si));
			if (tgtValueMap.getProperties(si) != null) 
				tgtProperties = new HashSet<String>(tgtValueMap.getProperties(si));

			for (int i = 0; i < len; i++) 
			{
				// Transform string uri into correspondent AbstractExpression
				String c1URI = i1Classes.get(i);
				if (c1URI.equals("http://www.w3.org/2002/07/owl#Thing")) {continue;}
				ClassId c1 = new ClassId(c1URI);

				// Add class to EntitySupport
				incrementEntitySupport(c1);

				// Only proceed to populate mappingSupport if there are any relationships 
				// for that instance besides class assignment
				Set<AttributeTypeRestriction> atrs = new HashSet<AttributeTypeRestriction>();

				if(o1.contains(c1URI))
					atrs.addAll(findATRs(si, tgtProperties, tgtValueMap));
				else 
					atrs.addAll(findATRs(si, srcProperties, srcValueMap));

				for(AttributeTypeRestriction atr: atrs)
				{
					incrementEntitySupport(atr);
					incrementMappingSupport(c1, atr);
				}
			}	
		}
	}

	/**
	 * Finds the possible AttributeTypeRestrictions for an instance
	 * @param instance: The URI of the instance
	 * @param properties: The set of data properties pertaining that instance
	 * @param vmap: The valueMap in which to look for (either source or target)
	 */
	private Set<AttributeTypeRestriction> findATRs(String instance, Set<String> properties, ValueMap vmap) 
	{
		// Since atrs is a set, no repeated elements will be added.
		Set<AttributeTypeRestriction> atrs = new HashSet<AttributeTypeRestriction>();

		if (properties != null) 
		{
			for(String pURI: properties) 
			{
				PropertyId p = new PropertyId(pURI, null);
				for(String v: vmap.getValues(instance, pURI)) 
				{
					OWLDatatype dataType = vmap.getDataType(instance, pURI, v);
					Datatype dt = new Datatype(dataType.toString());
					AttributeTypeRestriction atr = new AttributeTypeRestriction(p, dt);
					atrs.add(atr);
				}
			}
		}
		return atrs;
	}
}


