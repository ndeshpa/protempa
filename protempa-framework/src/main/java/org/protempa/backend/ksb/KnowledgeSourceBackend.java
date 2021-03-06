/*
 * #%L
 * Protempa Framework
 * %%
 * Copyright (C) 2012 - 2013 Emory University
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.protempa.backend.ksb;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.protempa.AbstractionDefinition;
import org.protempa.ContextDefinition;
import org.protempa.backend.Backend;
import org.protempa.backend.KnowledgeSourceBackendUpdatedEvent;
import org.protempa.KnowledgeSourceReadException;
import org.protempa.PropositionDefinition;
import org.protempa.TemporalPropositionDefinition;
import org.protempa.valueset.ValueSet;
import org.protempa.query.And;

/**
 * Translates from an arbitrary knowledge base to a PROTEMPA knowledge base.
 * Users of <code>KnowledgeSourceBackend</code> implementations must first call
 * {@link #initialize(java.util.Properties)} with a set of configuration properties that
 * is specific to the <code>KnowledgeSourceBackend</code> implementation.
 * 
 * @author Andrew Post
 */
public interface KnowledgeSourceBackend extends
        Backend<KnowledgeSourceBackendUpdatedEvent> {

    /**
     * Reads a proposition definition into the given PROTEMPA knowledge base.
     * This will only get called if the proposition definition has not already
     * been loaded.
     *
     * @param id
     *            a proposition id {@link String}. Guaranteed not
     *            <code>null</code>.
     * @return the {@link PropositionDefinition}, or <code>null</code> if none
     * with the given id was found.
     */
    PropositionDefinition readPropositionDefinition(String id)
            throws KnowledgeSourceReadException;
    
    AbstractionDefinition readAbstractionDefinition(String id)
            throws KnowledgeSourceReadException;
    
    ContextDefinition readContextDefinition(String id)
            throws KnowledgeSourceReadException;
    
    TemporalPropositionDefinition readTemporalPropositionDefinition(String id)
            throws KnowledgeSourceReadException;

    /**
     * Reads the value set from the knowledge base and returns it. This only
     * will get called if the value set has not already been loaded.
     * 
     * @param id
     *            The id of the value set to read
     * @return The ValueSet object
     */
    ValueSet readValueSet(String id)
            throws KnowledgeSourceReadException;
    
    String[] readAbstractedInto(String propId) throws KnowledgeSourceReadException;
    
    String[] readIsA(String propId) throws KnowledgeSourceReadException;
    
    String[] readInduces(String propId) throws KnowledgeSourceReadException;
    
    String[] readSubContextOfs(String propId) throws KnowledgeSourceReadException;
    
    Set<String> getKnowledgeSourceSearchResults(String searchKey) throws KnowledgeSourceReadException;

    Collection<String> collectPropIdDescendantsUsingAllNarrower(boolean inDataSourceOnly, String[] propIds) throws KnowledgeSourceReadException;

    Collection<PropositionDefinition> collectPropDefDescendantsUsingAllNarrower(boolean inDataSourceOnly, String[] propIds) throws KnowledgeSourceReadException;

    Collection<PropositionDefinition> collectPropDefDescendantsUsingInverseIsA(String[] propIds) throws KnowledgeSourceReadException;
    
    Collection<String> collectPropIdDescendantsUsingInverseIsA(String[] propIds) throws KnowledgeSourceReadException;

    List<PropositionDefinition> readPropositionDefinitions(String[] ids) throws KnowledgeSourceReadException;
    
    List<AbstractionDefinition> readAbstractionDefinitions(String[] ids) throws KnowledgeSourceReadException;

    List<ContextDefinition> readContextDefinitions(String[] toArray) throws KnowledgeSourceReadException;

    List<TemporalPropositionDefinition> readTemporalPropositionDefinitions(String[] toArray) throws KnowledgeSourceReadException;
   
}
