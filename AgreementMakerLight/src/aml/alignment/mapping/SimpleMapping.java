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
* A simple mapping between two entities of different Ontologies.              *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package aml.alignment.mapping;

import org.apache.commons.lang.StringEscapeUtils;

import aml.AML;
import aml.alignment.rdf.RDFElement;
import aml.settings.Namespace;

public class SimpleMapping extends Mapping<String>
{

//Constructors

	/**
	 * Creates a mapping between uri1 and uri2
	 * @param uri1: the uri of the source ontology entity
	 * @param uri2: the uri of the target ontology entity
	 */
	public SimpleMapping(String uri1, String uri2)
	{
		this(uri1,uri2,1.0,MappingRelation.EQUIVALENCE);
	}
	
	/**
	 * Creates a mapping between uri1 and uri2 with the given similarity
	 * @param uri1: the uri of the source ontology entity
	 * @param uri2: the uri of the target ontology entity
	 * @param sim: the similarity between the entities
	 */
	public SimpleMapping(String uri1, String uri2, double sim)
	{
		this(uri1,uri2,sim,MappingRelation.EQUIVALENCE);
	}
	
	/**
	 * Creates a mapping between uri1 and uri2 with the given similarity and relation
	 * @param uri1: the uri of the source ontology entity
	 * @param uri2: the uri of the target ontology entity
	 * @param sim: the similarity between the entities
	 * @param r: the mapping relationship between the entities
	 */
	public SimpleMapping(String uri1, String uri2, double sim, MappingRelation r)
	{
		super(uri1,uri2,sim,r);
	}
	
	
	/**
	 * Creates a new mapping that is a copy of m
	 * @param m: the mapping to copy
	 */
	public SimpleMapping(SimpleMapping m)
	{
		this(m.getEntity1(),m.getEntity2(),m.similarity,m.rel);
		this.status = m.status;
	}

//Public Methods

	
	@Override
	public String getEntity1()
	{
		return (String)entity1;
	}

	@Override
	public String getEntity2()
	{
		return (String)entity2;
	}

	/**
	 * Creates a copy of this mapping with the source and target
	 * entities swapped and with the inverse relationship.
	 * @return the new mapping
	 */
	public SimpleMapping reverse()
	{
		return new SimpleMapping(this.getEntity2(), this.getEntity1(), this.getSimilarity(), this.getRelationship().inverse());
	}

	@Override
	public String toRDF()
	{
		return "<map>\n" +
			"<Cell>\n" +
			"<entity1 " + RDFElement.RDF_RESOURCE.toRDF() + "=\"" + entity1 +"\"/>\n" +
			"<entity2 " + RDFElement.RDF_RESOURCE.toRDF() + "=\"" + entity2 +"\"/>\n" +
			"<measure " + RDFElement.RDF_DATATYPE.toRDF() + "=\"" + Namespace.XSD.prefix() + "float\">"+ similarity +"</measure>\n" +
			"<relation>" + StringEscapeUtils.escapeXml(rel.toString()) + "</relation>\n" +
			"</Cell>\n" +
			"</map>";
	}

	/**
	 * @return the Mapping in String form, as displayed by the AML GUI
	 */
	@Override
	public String toString()
	{
		return AML.getInstance().getSource().getName(getEntity1()) + " " +
			rel.toString() + " " + AML.getInstance().getTarget().getName(getEntity2()) +
			" (" + getSimilarityPercent() + ") ";
	}
	
	/**
	 * @return the Mapping in TSV form
	 */
	public String toTSV()
	{
		AML aml = AML.getInstance();
		String out = entity1 + "\t" + aml.getSource().getName(getEntity1()) +
				"\t" + entity2 + "\t" + aml.getTarget().getName(getEntity2()) +
				"\t" + similarity + "\t" + rel.toString();
		if(!status.equals(MappingStatus.UNKNOWN))
			out += "\t" + status;
		return out;
	}
}