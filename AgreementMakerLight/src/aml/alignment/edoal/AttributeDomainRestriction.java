/******************************************************************************
* Copyright 2013-2018 LASIGE                                                  *
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
* An AttributeDomainRestriction represents the set of individuals whose value *
* for a given relation falls under the specified restriction.                 *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package aml.alignment.edoal;

import java.util.Collection;
import java.util.Vector;

public class AttributeDomainRestriction extends ClassExpression
{

//Attributes
	
	private RelationExpression onAttribute;
	private ClassExpression rest;
	
//Constructor
	
	/**
	 * Constructs a new AttributeDomainRestriction on the given attribute with the class expression as range
	 * @param onAttribute: the restricted relation
	 * @param rest: the class expression restricting the range of the attribute
	 */
	public AttributeDomainRestriction(RelationExpression onAttribute, ClassExpression rest)
	{
		super();
		this.onAttribute = onAttribute;
		this.rest = rest;
		elements.addAll(onAttribute.getElements());
		elements.addAll(rest.getElements());
	}
	
//Public Methods
	
	@Override
	public boolean equals(Object o)
	{
		return o instanceof AttributeDomainRestriction &&
				((AttributeDomainRestriction)o).rest.equals(this.rest) &&
				((AttributeDomainRestriction)o).onAttribute.equals(this.onAttribute);
	}
	
	@Override
	public Collection<Expression> getComponents()
	{
		Vector<Expression> components = new Vector<Expression>();
		components.add(onAttribute);
		components.add(rest);
		return components;
	}
	
	@Override
	public String toRDF()
	{
		return "<edoal:AttributeDomainRestriction>\n" +
				"<onAttribute>\n" +
				onAttribute.toRDF() +
				"\n</onAttribute>\n" +
				"<edoal:class>\n" +
				rest.toRDF() +
				"\n</edoal:class>\n" +
				"</edoal:AttributeDomainRestriction>\n";
	}

	@Override
	public String toString()
	{
		return "range(" + onAttribute.toString() + ") " + rest.toString();
	}
}