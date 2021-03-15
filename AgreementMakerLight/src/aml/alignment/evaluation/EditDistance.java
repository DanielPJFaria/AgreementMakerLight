package aml.alignment.evaluation;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import aml.alignment.Alignment;
import aml.alignment.EDOALAlignment;
import aml.alignment.mapping.Mapping;
import aml.alignment.mapping.MappingStatus;
import aml.alignment.rdf.AbstractExpression;
import aml.util.similarity.Similarity;

public class EditDistance implements Evaluator
{
	//Attributes
	Set<String> restrictions;

	//Constructor
	public EditDistance() 
	{
		Set<String> restrictions = new HashSet<String>();
		restrictions.add("greater-than");
	}

	//Public methods
	/**
	 * This method evaluates an EDOAL Alignment under the edit-distance premise, given a reference alignment
	 * @param alignment: the Alignment we want to evaluate
	 * @param reference: the reference Alignment that we will use for comparison
	 * @return the evaluation of this Alignment {# correct mappings, # conflict mappings}
	 */
	@SuppressWarnings("rawtypes")
	public int[] evaluate(Alignment alignment, Alignment reference) 
	{
		System.out.println("Performing Evaluation");
		long time = System.currentTimeMillis()/1000;
		EDOALAlignment algn = (EDOALAlignment)alignment;
		EDOALAlignment ref = (EDOALAlignment)reference;

		int[] count = new int[2];
		for(Mapping<AbstractExpression> m : algn) 
		{
			// Exact matches
			if(ref.contains(m))
			{
				System.out.println("Exact match!");
				count[0]++;
				m.setStatus(MappingStatus.CORRECT);
			}
			// Jaccard similarity
			else
			{
				// Construct a vector containing the single entities, relationship and constructors of the mapping being evaluated
				Vector<String> algnElements = new Vector<String>(m.getEntity1().getElements());
				algnElements.addAll(m.getEntity2().getElements());
				algnElements.add(m.getRelationship().toString());
				addConstructors(m, algnElements);

				// Choose which reference mappings to compare it with
				Vector<Mapping<AbstractExpression>> srcRefMappings = new Vector<Mapping<AbstractExpression>>();
				Vector<Mapping<AbstractExpression>> tgtRefMappings = new Vector<Mapping<AbstractExpression>>();

				for (String e: m.getEntity1().getElements()) 
					srcRefMappings.addAll(ref.getSourceMappings(e));	 
				for (String e: m.getEntity2().getElements()) 
					tgtRefMappings = ref.getTargetMappings(e);
				if(srcRefMappings.size()==0 || tgtRefMappings.size()==0)
					continue;

				// Only consider reference mappings that have at least one common 
				// src and tgt entity to the mapping being evaluated
				srcRefMappings.retainAll(tgtRefMappings);
				if(srcRefMappings.size()==0)
					continue;

				System.out.println("------------------------");
				System.out.println("algnMapping: "+m);
				System.out.println("algnSet: "+algnElements);
				System.out.println("refMappings: "+srcRefMappings);

				// Find the maximum Jaccard similarity between the ref mappings and the mapping being evaluated
				double max = 0;
				for (Mapping<AbstractExpression> mRef: srcRefMappings) 
				{
					// Construct a vector containing the single entities, relationship and constructors of the reference mapping
					Vector<String> refElements = new Vector<String>(mRef.getEntity1().getElements());
					refElements.addAll(mRef.getEntity2().getElements());
					refElements.add(mRef.getRelationship().toString());
					addConstructors(mRef, refElements);

					System.out.println("refSet"+refElements);
					double sim = Similarity.weightedJaccardSimilarity(algnElements, refElements);
					if (sim > max)
						max = sim;
				}
				System.out.println("Jaccard: "+ max);
				count[0]+= max;
			}
		}
		System.out.println("Finished in " +	(System.currentTimeMillis()/1000-time) + " seconds");
		return count;
	}

	/**
	 * This method adds the constructors present in a complex correspondence to the Vector
	 * @param mapping: the mapping of which we extract the constructors
	 * @param elements: the Vector to which the constructor will be added to
	 */
	private void addConstructors(Mapping<AbstractExpression> mapping, Vector<String> elements) 
	{
		Vector<String> constructors = new Vector<String>();
		String m = mapping.toString();

		constructors.addAll(searchKeyword(m, "AND"));
		constructors.addAll(searchKeyword(m, "OR"));
		constructors.addAll(searchKeyword(m, "NOT"));
		constructors.addAll(searchKeyword(m, "PATH"));
		constructors.addAll(searchKeyword(m, "INVERSE"));
		constructors.addAll(searchKeyword(m, "greater-than "));
		elements.addAll(constructors);
	}

	/**
	 * This method searches for some keyword in a string and add
	 * @param s: string in which to search for the keyword
	 * @param keyword: keyword to search for
	 * @return a Vector containing the keyword, as many times as it occurs in the string
	 */
	private Vector<String> searchKeyword(String s, String keyword) 
	{
		String[] split = s.split(keyword);
		Vector<String> result = new Vector<String>();

		if (split.length > 1) 
		{
			if(keyword =="greater-than ") 
			{
				keyword += split[1].substring(1, 2);
			}
			// also add other value expressions
			
			for (int i=0; i<(split.length-1); i++) 
				result.add(keyword);
		}
		return result;
	}
}