package org.protempa.proposition;

import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

import org.protempa.DataSourceType;
import org.protempa.QuerySession;
import org.protempa.proposition.value.Value;

/**
 * A data element. All implementations of this have property change support
 * methods, but no change events are fired at present.
 * 
 * In Proposition, the equals() method is used to compare two instances of 
 * Proposition.  Two propositions are equal if they have the same 
 * datasource backend id, and unique identifier.  If either instance has a 
 * null datasource backend id, or null unique identifier, a object refernce 
 * check is made for equality.
 * 
 * @author Andrew Post
 */
public interface Proposition extends PropositionVisitable,
        PropositionCheckedVisitable, Serializable {

    /**
     * Gets this proposition's data type.
     * 
     * @return the identification {@link String} for the type of data
     *         represented by this proposition.
     */
    String getId();

    /**
     * Returns the data source type of the Proposition.
     * @return a {@link DataSourceType}.
     */
    DataSourceType getDataSourceType();

    /**
     * Adds a {@link PropertyChangeListener} to the listener list. The listener
     * is registered for all bound properties of this class (none at present).
     * 
     * If listener is null, no exception is thrown and no action is performed.
     * 
     * @param l
     *            the {@link PropertyChangeListener} to be added.
     */
    void addPropertyChangeListener(PropertyChangeListener l);

    /**
     * Removes a {@link PropertyChangeListener} from the listener list. This
     * method should be used to remove {@link PropertyChangeListener}s that were
     * registered for all bound properties of this class.
     * 
     * If listener is null, no exception is thrown and no action is performed.
     * 
     * @param l
     *            the {@link PropertyChangeListener} to be removed
     */
    void removePropertyChangeListener(PropertyChangeListener l);

    /**
     * Determines if the specified object is a {@link Proposition} and has
     * the same field values as this proposition.
     * 
     * @param prop an {@link Object}.
     * @return <code>true</code> if the specified object is a
     * {@link Proposition} and has the same field values, <code>false</code>
     * otherwise.
     */
    boolean isEqual(Object prop);

    /**
     * Gets the value of the specified property.
     *
     * @param name the name {@link String} of a valid property.
     * @return the {@link Value} of the specified property.
     */
    Value getProperty(String name);

    /**
     * Gets the global unique identifiers for the propositions that have the
     * specified 1:N relationship with this proposition.
     *
     * @param name the name of the relationship.
     * @return a {@link List<UniqueIdentifier>} of global unique identifiers.
     * Guaranteed not <code>null</code>.
     * @see QuerySession#getReferences(org.protempa.proposition.Proposition,
     * java.lang.String) to get the propositions with the specified
     * relationship.
     */
    List<UniqueIdentifier> getReferences(String name);

    /**
     * Get a set of property names from the proposition
     * 
     * @return a set containing the names of all the properties contained
     */
    Set<String> getPropertyNames();

    /**
     * Get a set of reference names from the proposition
     * 
     * @return a set containing the names of all the references contained
     */
    Set<String> getReferenceNames();

    /**
     * Gets this proposition's global unique identifier.
     *
     * @return a {@link UniqueIdentifier}.
     */
    UniqueIdentifier getUniqueIdentifier();
}
