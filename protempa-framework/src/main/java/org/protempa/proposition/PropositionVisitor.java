package org.protempa.proposition;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Interface for classes that do processing on propositions.
 * 
 * @author Andrew Post
 * 
 */
public interface PropositionVisitor {
	/**
	 * Processes results from a PROTEMPA finder method.
	 * 
	 * @param finderResult
	 *            a {@link Map<String, List<Proposition>>}.
	 */
	void visit(Map<String, List<Proposition>> finderResult);

	/**
	 * Processes a collection of propositions.
	 * 
	 * @param propositions
	 *            a {@link Collection<Proposition>}. Cannot be
	 *            <code>null</code>.
	 */
	void visit(Collection<? extends Proposition> propositions);

	/**
	 * Processes a primitive parameter.
	 * 
	 * @param primitiveParameter
	 *            a {@link PrimitiveParameter}. Cannot be <code>null</code>.
	 */
	void visit(PrimitiveParameter primitiveParameter);

	/**
	 * Processes an event.
	 * 
	 * @param event
	 *            an {@link Event}. Cannot be <code>null</code>.
	 */
	void visit(Event event);

	/**
	 * Processes an abstract parameter.
	 * 
	 * @param abstractParameter
	 *            an {@link AbstractParameter}. Cannot be <code>null</code>.
	 */
	void visit(AbstractParameter abstractParameter);

	/**
	 * Processes a constant parameter.
	 * 
	 * @param constantParameter
	 *            an {@link ConstantParameter}. Cannot be <code>null</code>.
	 */
	void visit(ConstantProposition constantParameter);

	/**
	 * Processes a context.
	 * 
	 * @param context
	 *            a {@link Context}. Cannot be <code>null</code>.
	 */
	void visit(Context context);
}
