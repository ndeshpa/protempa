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
package org.protempa;

import org.protempa.valueset.ValueSet;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.logging.Level;
import org.apache.commons.collections4.map.ReferenceMap;
import org.apache.commons.lang3.StringUtils;
import org.arp.javautil.arrays.Arrays;
import org.protempa.backend.BackendInitializationException;
import org.protempa.backend.BackendNewInstanceException;
import org.protempa.backend.KnowledgeSourceBackendUpdatedEvent;
import org.protempa.backend.ksb.KnowledgeSourceBackend;
import org.protempa.query.And;

/**
 * A read-only "interface" to an externally-maintained knowledge base. The user
 * must specify a backend in the constructor from where information about
 * primitive parameters and abstract parameters can be obtained.
 *
 * @author Andrew Post
 */
public final class KnowledgeSourceImpl
        extends AbstractSource<KnowledgeSourceUpdatedEvent, KnowledgeSourceBackend, KnowledgeSourceUpdatedEvent, KnowledgeSourceBackendUpdatedEvent> implements KnowledgeSource {

    /**
     * PROTEMPA knowledge base.
     */
    private PropositionDefinitionCache propositionDefinitionCache;
    private final Map<String, Object> notFoundAbstractionDefinitionRequests;
    private final Map<String, Object> notFoundValueSetRequests;
    private final Map<String, Object> notFoundContextDefinitionRequests;
    private final Map<String, Object> notFoundPropositionDefinitionRequests;
    private final Map<String, Object> notFoundTemporalPropositionDefinitionRequests;
    private final PropositionDefinitionReader propDefReader;
    private final AbstractionDefinitionReader abstractionDefReader;
    private final ContextDefinitionReader contextDefReader;
    private final TemporalPropositionDefinitionReader tempPropDefReader;
    private final Map<PropositionDefinition, List<PropositionDefinition>> inverseIsACache;
    private final Map<AbstractionDefinition, List<PropositionDefinition>> abstractedFromCache;
    private final Map<ContextDefinition, List<ContextDefinition>> subContextsCache;
    private final Map<ContextDefinition, List<TemporalPropositionDefinition>> inducedByCache;
    private SubtreePropositionDefinitionGetterRegular inDataSourceGetter;
    private SubtreePropositionDefinitionGetterRegular collectSubtreeGetter;

    public KnowledgeSourceImpl(KnowledgeSourceBackend... backends) {
        super(backends);
        this.inverseIsACache = new ReferenceMap<>();
        this.abstractedFromCache = new ReferenceMap<>();
        this.subContextsCache = new ReferenceMap<>();
        this.inducedByCache = new ReferenceMap<>();
        this.notFoundAbstractionDefinitionRequests
                = new WeakHashMap<>();
        this.notFoundValueSetRequests = new WeakHashMap<>();
        this.notFoundPropositionDefinitionRequests
                = new WeakHashMap<>();
        this.notFoundContextDefinitionRequests
                = new WeakHashMap<>();
        this.notFoundTemporalPropositionDefinitionRequests
                = new HashMap<>();

        this.propDefReader = new PropositionDefinitionReader();
        this.abstractionDefReader = new AbstractionDefinitionReader();
        this.contextDefReader = new ContextDefinitionReader();
        this.tempPropDefReader = new TemporalPropositionDefinitionReader();
    }

    /**
     * Connect to the knowledge source backend(s).
     */
    private void initializeIfNeeded() throws BackendInitializationException,
            BackendNewInstanceException {
        if (isClosed()) {
            throw new IllegalStateException("Knowledge source already closed!");
        }
        if (this.propositionDefinitionCache == null) {
            this.propositionDefinitionCache = new PropositionDefinitionCache();
        }
        if (this.inDataSourceGetter == null) {
            this.inDataSourceGetter
                    = new SubtreePropositionDefinitionGetterRegular(this, true);
        }
        if (this.collectSubtreeGetter == null) {
            this.collectSubtreeGetter
                    = new SubtreePropositionDefinitionGetterRegular(this, false);
        }
    }

    private void initializeIfNeeded(String template, String... propIds)
            throws KnowledgeSourceReadException {
        try {
            initializeIfNeeded();
        } catch (BackendInitializationException | BackendNewInstanceException ex) {
            String action = MessageFormat.format(template, StringUtils.join(propIds, ", "));
            throw new KnowledgeSourceReadException(
                    "An error occurred " + action, ex);
        }
    }

    private void initializeIfNeeded(String action)
            throws KnowledgeSourceReadException {
        initializeIfNeeded(action, null);
    }

    @Override
    public List<PropositionDefinition> readInverseIsA(
            PropositionDefinition propDef)
            throws KnowledgeSourceReadException {
        if (propDef == null) {
            throw new IllegalArgumentException("propDef cannot be null");
        }
        synchronized (this.inverseIsACache) {
            List<PropositionDefinition> result = this.inverseIsACache.get(propDef);
            if (result != null) {
                return result;
            } else {
                result = new ArrayList<>();
                initializeIfNeeded("reading inverseIsA of {0}", propDef.getId());
                String[] propIds = propDef.getInverseIsA();
                for (String propId : propIds) {
                    PropositionDefinition pd = readPropositionDefinition(propId);
                    assert pd != null :
                            "Proposition definition " + propId
                            + ", which is specified as a child of "
                            + propDef.getId() + ", does not exist";
                    if (pd != null) {
                        result.add(pd);
                    }
                }
                result = Collections.unmodifiableList(result);
                this.inverseIsACache.put(propDef, result);
                return result;
            }
        }
    }

    @Override
    public List<PropositionDefinition> readIsA(PropositionDefinition propDef)
            throws KnowledgeSourceReadException {
        if (propDef == null) {
            throw new IllegalArgumentException("propDef cannot be null");
        }

        return readIsA(propDef.getId());
    }
    
    @Override
    public List<String> readIsAPropIds(PropositionDefinition propDef)
            throws KnowledgeSourceReadException {
        if (propDef == null) {
            throw new IllegalArgumentException("propDef cannot be null");
        }

        return readIsAPropIds(propDef.getId());
    }

    @Override
    public List<PropositionDefinition> readInverseIsA(String id)
            throws KnowledgeSourceReadException {
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }
        PropositionDefinition propDef = readPropositionDefinition(id);
        if (propDef == null) {
            return Collections.emptyList();
        } else {
            return readInverseIsA(propDef);
        }
    }

    @Override
    public List<PropositionDefinition> readIsA(String id)
            throws KnowledgeSourceReadException {
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }
        Set<String> isAs = new HashSet<>();
        for (KnowledgeSourceBackend backend : getBackends()) {
            Arrays.addAll(isAs, backend.readIsA(id));
        }
        List<PropositionDefinition> result
                = new ArrayList<>();
        for (String isAPropId : isAs) {
            result.add(readPropositionDefinition(isAPropId));
        }

        return result;
    }
    
    @Override
    public List<String> readIsAPropIds(String id)
            throws KnowledgeSourceReadException {
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }
        Set<String> isAs = new HashSet<>();
        for (KnowledgeSourceBackend backend : getBackends()) {
            Arrays.addAll(isAs, backend.readIsA(id));
        }
        return new ArrayList<>(isAs);
    }

    @Override
    public List<PropositionDefinition> readAbstractedFrom(
            AbstractionDefinition propDef)
            throws KnowledgeSourceReadException {
        if (propDef == null) {
            throw new IllegalArgumentException("propDef cannot be null");
        }
        synchronized (this.abstractedFromCache) {
            List<PropositionDefinition> result
                    = this.abstractedFromCache.get(propDef);
            if (result != null) {
                return result;
            } else {
                result = new ArrayList<>();
                initializeIfNeeded("reading abstractedFrom of {0}",
                        propDef.getId());

                Set<String> propIds = propDef.getAbstractedFrom();
                for (String propId : propIds) {
                    PropositionDefinition pd = readPropositionDefinition(propId);
                    assert pd != null :
                            "Proposition definition " + propId
                            + ", which " + propDef.getId()
                            + "is specified as abstracted from, does not exist";
                    if (pd != null) {
                        result.add(pd);
                    }
                }
                result = Collections.unmodifiableList(result);
                if (propDef != null) {
                    this.abstractedFromCache.put(propDef, result);
                }
                return result;
            }
        }
    }

    @Override
    public List<AbstractionDefinition> readAbstractedInto(
            PropositionDefinition propDef)
            throws KnowledgeSourceReadException {
        if (propDef == null) {
            throw new IllegalArgumentException("propDef cannot be null");
        }

        return readAbstractedInto(propDef.getId());
    }
    
    @Override
    public List<String> readAbstractedIntoPropIds(
            PropositionDefinition propDef)
            throws KnowledgeSourceReadException {
        if (propDef == null) {
            throw new IllegalArgumentException("propDef cannot be null");
        }

        return readAbstractedIntoPropIds(propDef.getId());
    }

    @Override
    public List<PropositionDefinition> readAbstractedFrom(String id)
            throws KnowledgeSourceReadException {
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }
        AbstractionDefinition propDef = readAbstractionDefinition(id);
        if (propDef == null) {
            return Collections.emptyList();
        } else {
            return readAbstractedFrom(propDef);
        }
    }

    @Override
    public List<AbstractionDefinition> readAbstractedInto(String id)
            throws KnowledgeSourceReadException {
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }
        Set<String> abstractedIntos = new HashSet<>();
        for (KnowledgeSourceBackend backend : getBackends()) {
            Arrays.addAll(abstractedIntos,
                    backend.readAbstractedInto(id));
        }
        List<AbstractionDefinition> result
                = new ArrayList<>();
        for (String abstractedIntoPropId : abstractedIntos) {
            AbstractionDefinition def
                    = readAbstractionDefinition(abstractedIntoPropId);
            if (def == null) {
                throw new KnowledgeSourceReadException(
                        "Invalid proposition id: " + abstractedIntoPropId);
            }
            result.add(def);
        }

        return result;
    }
    
    @Override
    public List<String> readAbstractedIntoPropIds(String id)
            throws KnowledgeSourceReadException {
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }
        Set<String> abstractedIntos = new HashSet<>();
        for (KnowledgeSourceBackend backend : getBackends()) {
            Arrays.addAll(abstractedIntos,
                    backend.readAbstractedInto(id));
        }
        return new ArrayList<>(abstractedIntos);
    }

    @Override
    public List<ContextDefinition> readInduces(
            TemporalPropositionDefinition propDef)
            throws KnowledgeSourceReadException {
        if (propDef == null) {
            throw new IllegalArgumentException("propDef cannot be null");
        }

        return readInduces(propDef.getId());
    }
    
    @Override
    public List<String> readInducesPropIds(
            TemporalPropositionDefinition propDef)
            throws KnowledgeSourceReadException {
        if (propDef == null) {
            throw new IllegalArgumentException("propDef cannot be null");
        }

        return readInducesPropIds(propDef.getId());
    }

    @Override
    public List<ContextDefinition> readInduces(String id)
            throws KnowledgeSourceReadException {
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }
        Set<String> induces = new HashSet<>();
        for (KnowledgeSourceBackend backend : getBackends()) {
            Arrays.addAll(induces,
                    backend.readInduces(id));
        }
        List<ContextDefinition> result
                = new ArrayList<>();
        for (String inducesPropId : induces) {
            ContextDefinition def
                    = readContextDefinition(inducesPropId);
            if (def == null) {
                throw new KnowledgeSourceReadException(
                        "Invalid proposition id: " + inducesPropId);
            }
            result.add(def);
        }

        return result;
    }
    
    @Override
    public List<String> readInducesPropIds(String id)
            throws KnowledgeSourceReadException {
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }
        Set<String> induces = new HashSet<>();
        for (KnowledgeSourceBackend backend : getBackends()) {
            Arrays.addAll(induces,
                    backend.readInduces(id));
        }
        return new ArrayList<>(induces);
    }

    @Override
    public List<ContextDefinition> readSubContextOfs(String id)
            throws KnowledgeSourceReadException {
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }
        Set<String> subContextOfs = new HashSet<>();
        for (KnowledgeSourceBackend backend : getBackends()) {
            Arrays.addAll(subContextOfs,
                    backend.readSubContextOfs(id));
        }
        List<ContextDefinition> result
                = new ArrayList<>();
        for (String subContextOfPropId : subContextOfs) {
            ContextDefinition def
                    = readContextDefinition(subContextOfPropId);
            if (def == null) {
                throw new KnowledgeSourceReadException(
                        "Invalid context id: " + subContextOfPropId);
            }
            result.add(def);
        }

        return result;
    }
    
    @Override
    public List<String> readSubContextOfPropIds(String id)
            throws KnowledgeSourceReadException {
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }
        Set<String> subContextOfs = new HashSet<>();
        for (KnowledgeSourceBackend backend : getBackends()) {
            Arrays.addAll(subContextOfs,
                    backend.readSubContextOfs(id));
        }
        return new ArrayList<>(subContextOfs);
    }

    @Override
    public List<ContextDefinition> readSubContextOfs(ContextDefinition cd)
            throws KnowledgeSourceReadException {
        if (cd == null) {
            throw new IllegalArgumentException("cd cannot be null");
        }
        return readSubContextOfs(cd.getId());
    }
    
    @Override
    public List<String> readSubContextOfPropIds(ContextDefinition cd)
            throws KnowledgeSourceReadException {
        if (cd == null) {
            throw new IllegalArgumentException("cd cannot be null");
        }
        return readSubContextOfPropIds(cd.getId());
    }

    @Override
    public List<ContextDefinition> readSubContexts(String contextId) throws KnowledgeSourceReadException {
        if (contextId == null) {
            throw new IllegalArgumentException("contextId cannot be null");
        }
        ContextDefinition pd = readContextDefinition(contextId);
        if (pd != null) {
            return readSubContexts(pd);
        } else {
            return new ArrayList<>(0);
        }

    }

    @Override
    public List<ContextDefinition> readSubContexts(ContextDefinition contextDef) throws KnowledgeSourceReadException {
        if (contextDef == null) {
            throw new IllegalArgumentException("contextDef cannot be null");
        }
        synchronized (this.subContextsCache) {
            List<ContextDefinition> result
                    = this.subContextsCache.get(contextDef);
            if (result != null) {
                return result;
            } else {
                result = new ArrayList<>();
                initializeIfNeeded("reading subContexts of {0}",
                        contextDef.getId());

                String[] propIds = contextDef.getSubContexts();
                for (String propId : propIds) {
                    ContextDefinition pd = readContextDefinition(propId);
                    assert pd != null :
                            "Context definition " + propId
                            + ", which " + contextDef.getId()
                            + "is specified as abstracted from, does not exist";
                    if (pd != null) {
                        result.add(pd);
                    }
                }
                result = Collections.unmodifiableList(result);
                if (contextDef != null) {
                    this.subContextsCache.put(contextDef, result);
                }
                return result;
            }
        }
    }

    @Override
    public List<PropositionDefinition> readParents(
            PropositionDefinition propDef)
            throws KnowledgeSourceReadException {
        if (propDef == null) {
            throw new IllegalArgumentException("propDef cannot be null");
        }
        
        List<String> result = readParentPropIds(propDef);
        
        return readPropositionDefinitions(result.toArray(new String[result.size()]));
    }
    
    @Override
    public List<String> readParentPropIds(PropositionDefinition propDef)
            throws KnowledgeSourceReadException {
        if (propDef == null) {
            throw new IllegalArgumentException("propDef cannot be null");
        }
        List<String> subContextOfs;
        List<String> induces;
        if (propDef instanceof ContextDefinition) {
            subContextOfs = readSubContextOfPropIds((ContextDefinition) propDef);
            induces = readInducesPropIds((ContextDefinition) propDef);
        } else {
            subContextOfs = Collections.emptyList();
            induces = Collections.emptyList();
        }

        List<String> ad = readAbstractedIntoPropIds(propDef);
        List<String> pd = readIsAPropIds(propDef);
        Set<String> result = new HashSet<>();
        result.addAll(subContextOfs);
        result.addAll(induces);
        result.addAll(ad);
        result.addAll(pd);
        return new ArrayList<>(result);
    }

    @Override
    public List<PropositionDefinition> readParents(String propId)
            throws KnowledgeSourceReadException {
        if (propId == null) {
            throw new IllegalArgumentException("propId cannot be null");
        }
        PropositionDefinition pd = readPropositionDefinition(propId);
        if (pd != null) {
            return readParents(pd);
        } else {
            return new ArrayList<>(0);
        }
    }
    
    @Override
    public List<String> readParentPropIds(String propId)
            throws KnowledgeSourceReadException {
        if (propId == null) {
            throw new IllegalArgumentException("propId cannot be null");
        }
        PropositionDefinition pd = readPropositionDefinition(propId);
        if (pd != null) {
            return readParentPropIds(pd);
        } else {
            return new ArrayList<>(0);
        }
    }

    @Override
    public List<TemporalPropositionDefinition> readInducedBy(String contextId) throws KnowledgeSourceReadException {
        if (contextId == null) {
            throw new IllegalArgumentException("contextId cannot be null");
        }
        ContextDefinition pd = readContextDefinition(contextId);
        if (pd != null) {
            return readInducedBy(pd);
        } else {
            return new ArrayList<>(0);
        }
    }

    @Override
    public List<TemporalPropositionDefinition> readInducedBy(ContextDefinition contextDef) throws KnowledgeSourceReadException {
        if (contextDef == null) {
            throw new IllegalArgumentException("contextDef cannot be null");
        }
        synchronized (this.inducedByCache) {
            List<TemporalPropositionDefinition> result
                    = this.inducedByCache.get(contextDef);
            if (result != null) {
                return result;
            } else {
                result = new ArrayList<>();
                initializeIfNeeded("reading inducedBy of {0}",
                        contextDef.getId());

                TemporalExtendedPropositionDefinition[] propIds = contextDef.getInducedBy();
                for (TemporalExtendedPropositionDefinition tepd : propIds) {
                    TemporalPropositionDefinition pd = readTemporalPropositionDefinition(tepd.getPropositionId());
                    assert pd != null :
                            "Proposition definition " + tepd.getPropositionId()
                            + ", which " + contextDef.getId()
                            + "is specified as induced by, does not exist";
                    if (pd != null) {
                        result.add(pd);
                    }
                }
                result = Collections.unmodifiableList(result);
                if (contextDef != null) {
                    this.inducedByCache.put(contextDef, result);
                }
                return result;
            }
        }
    }

    @Override
    public boolean hasTemporalPropositionDefinition(String id) throws KnowledgeSourceReadException {
        return this.tempPropDefReader.has(id);
    }

    @Override
    public TemporalPropositionDefinition readTemporalPropositionDefinition(String id) throws KnowledgeSourceReadException {
        return this.tempPropDefReader.read(id);
    }

    @Override
    public List<TemporalPropositionDefinition> readTemporalPropositionDefinitions(String... propIds) throws KnowledgeSourceReadException {
        return this.tempPropDefReader.read(propIds);
    }

    private abstract class AbstractDefinitionReader<E extends PropositionDefinition> {

        final E read(String id) throws KnowledgeSourceReadException {
            if (id == null) {
                throw new IllegalArgumentException("id cannot be null");
            }
            initializeIfNeeded("reading the proposition definition {0}", id);

            E result = null;
            synchronized (this) {
                if (!isInNotFound(id)) {
                    if (propositionDefinitionCache != null) {
                        result = readFromKnowledgeBase(id);
                        if (result == null) {
                            for (KnowledgeSourceBackend backend : getBackends()) {
                                result = readFromBackend(id, backend);
                                if (result != null) {
                                    try {
                                        putInKnowledgeBase(result);
                                    } catch (InvalidPropositionIdException ex) {
                                        ProtempaUtil.logger().log(Level.SEVERE, "Error adding proposition definition to cache", ex);
                                        throw new AssertionError("Error adding proposition definition to cache: " + ex.getMessage());
                                    }
                                    return result;
                                }
                            }
                            putInNotFound(id);
                        }
                    }
                }
            }
            return result;
        }

        final List<E> read(String[] ids) throws KnowledgeSourceReadException {
            assert ids != null : "ids cannot be null";
            initializeIfNeeded("reading the proposition definitions {0}", ids);

            List<E> result = new ArrayList<>();
            synchronized (this) {
                List<String> propIdsToFind = new ArrayList<>();
                for (String id : ids) {
                    if (!isInNotFound(id)) {
                        if (propositionDefinitionCache != null) {
                            E propDef = readFromKnowledgeBase(id);
                            if (propDef == null) {
                                propIdsToFind.add(id);
                            } else {
                                result.add(propDef);
                            }
                        }
                    }
                }

                List<E> resultNew = new ArrayList<>();
                for (KnowledgeSourceBackend backend : getBackends()) {
                    resultNew.addAll(readFromBackend(propIdsToFind, backend));
                }
                try {
                    putInKnowledgeBase(resultNew);
                } catch (InvalidPropositionIdException ex) {
                    throw new AssertionError("Error adding proposition definition to cache: " + ex.getMessage());
                }
                result.addAll(resultNew);
            }
            return result;
        }

        final boolean has(String id) throws KnowledgeSourceReadException {
            if (id == null) {
                throw new IllegalArgumentException("id cannot be null");
            }
            return read(id) != null;
        }

        protected abstract boolean isInNotFound(String id);

        protected abstract void putInNotFound(String id);

        protected abstract E readFromKnowledgeBase(String id);

        protected abstract E readFromBackend(String id,
                KnowledgeSourceBackend backend)
                throws KnowledgeSourceReadException;

        protected abstract List<E> readFromBackend(List<String> ids,
                KnowledgeSourceBackend backend)
                throws KnowledgeSourceReadException;

        protected abstract void putInKnowledgeBase(E propDef)
                throws InvalidPropositionIdException;

        void putInKnowledgeBase(List<E> propDefs)
                throws InvalidPropositionIdException {
            for (E propDef : propDefs) {
                putInKnowledgeBase(propDef);
            }
        }

    }

    private final class PropositionDefinitionReader extends AbstractDefinitionReader<PropositionDefinition> {

        @Override
        protected PropositionDefinition readFromKnowledgeBase(String id) {
            synchronized (propositionDefinitionCache) {
                return propositionDefinitionCache.getPropositionDefinition(id);
            }
        }

        @Override
        protected PropositionDefinition readFromBackend(String id,
                KnowledgeSourceBackend backend)
                throws KnowledgeSourceReadException {
            return backend.readPropositionDefinition(id);
        }

        @Override
        protected List<PropositionDefinition> readFromBackend(List<String> ids, KnowledgeSourceBackend backend) throws KnowledgeSourceReadException {
            return backend.readPropositionDefinitions(ids.toArray(new String[ids.size()]));
        }

        @Override
        protected boolean isInNotFound(String id) {
            return notFoundPropositionDefinitionRequests.containsKey(id);
        }

        @Override
        protected void putInNotFound(String id) {
            notFoundPropositionDefinitionRequests.put(id, null);
        }

        @Override
        protected void putInKnowledgeBase(PropositionDefinition propDef) throws InvalidPropositionIdException {
            synchronized (propositionDefinitionCache) {
                propositionDefinitionCache.addPropositionDefinition(propDef);
            }
        }

    }

    private final class AbstractionDefinitionReader
            extends AbstractDefinitionReader<AbstractionDefinition> {

        @Override
        protected AbstractionDefinition readFromKnowledgeBase(String id) {
            synchronized (propositionDefinitionCache) {
                AbstractionDefinition result
                        = propositionDefinitionCache.getAbstractionDefinition(id);
                if (result == null) {
                    PropositionDefinition r
                            = propositionDefinitionCache.getPropositionDefinition(id);
                    if (r instanceof AbstractionDefinition) {
                        try {
                            propositionDefinitionCache
                                    .addAbstractionDefinition((AbstractionDefinition) r);
                        } catch (InvalidPropositionIdException ex) {
                            throw new AssertionError("Should not happen");
                        }
                        result = (AbstractionDefinition) r;
                    }
                }
                return result;
            }
        }

        @Override
        protected AbstractionDefinition readFromBackend(String id,
                KnowledgeSourceBackend backend)
                throws KnowledgeSourceReadException {
            return backend.readAbstractionDefinition(id);
        }

        @Override
        protected List<AbstractionDefinition> readFromBackend(List<String> ids, KnowledgeSourceBackend backend) throws KnowledgeSourceReadException {
            return backend.readAbstractionDefinitions(ids.toArray(new String[ids.size()]));
        }

        @Override
        protected boolean isInNotFound(String id) {
            return notFoundAbstractionDefinitionRequests.containsKey(id);
        }

        @Override
        protected void putInNotFound(String id) {
            notFoundAbstractionDefinitionRequests.put(id, null);
        }

        @Override
        protected void putInKnowledgeBase(AbstractionDefinition propDef) throws InvalidPropositionIdException {
            synchronized (propositionDefinitionCache) {
                propositionDefinitionCache.addAbstractionDefinition(propDef);
                propositionDefinitionCache.addTemporalPropositionDefinition(propDef);
                propositionDefinitionCache.addPropositionDefinition(propDef);
            }
        }
    }

    private final class ContextDefinitionReader
            extends AbstractDefinitionReader<ContextDefinition> {

        @Override
        protected boolean isInNotFound(String id) {
            return notFoundContextDefinitionRequests.containsKey(id);
        }

        @Override
        protected void putInNotFound(String id) {
            notFoundContextDefinitionRequests.put(id, null);
        }

        @Override
        protected ContextDefinition readFromKnowledgeBase(String id) {
            synchronized (propositionDefinitionCache) {
                ContextDefinition result
                        = propositionDefinitionCache.getContextDefinition(id);
                if (result == null) {
                    PropositionDefinition r
                            = propositionDefinitionCache.getPropositionDefinition(id);
                    if (r instanceof ContextDefinition) {
                        try {
                            propositionDefinitionCache
                                    .addContextDefinition((ContextDefinition) r);
                        } catch (InvalidPropositionIdException ex) {
                            throw new AssertionError("Should not happen");
                        }
                        result = (ContextDefinition) r;
                    }
                }
                return result;
            }
        }

        @Override
        protected ContextDefinition readFromBackend(String id, KnowledgeSourceBackend backend) throws KnowledgeSourceReadException {
            return backend.readContextDefinition(id);
        }

        @Override
        protected List<ContextDefinition> readFromBackend(List<String> ids, KnowledgeSourceBackend backend) throws KnowledgeSourceReadException {
            return backend.readContextDefinitions(ids.toArray(new String[ids.size()]));
        }

        @Override
        protected void putInKnowledgeBase(ContextDefinition propDef) throws InvalidPropositionIdException {
            synchronized (propositionDefinitionCache) {
                propositionDefinitionCache.addContextDefinition(propDef);
                propositionDefinitionCache.addPropositionDefinition(propDef);
            }
        }
    }

    private final class TemporalPropositionDefinitionReader
            extends AbstractDefinitionReader<TemporalPropositionDefinition> {

        @Override
        protected boolean isInNotFound(String id) {
            return notFoundTemporalPropositionDefinitionRequests.containsKey(id);
        }

        @Override
        protected void putInNotFound(String id) {
            notFoundTemporalPropositionDefinitionRequests.put(id, null);
        }

        @Override
        protected TemporalPropositionDefinition readFromKnowledgeBase(String id) {
            synchronized (propositionDefinitionCache) {
                TemporalPropositionDefinition result
                        = propositionDefinitionCache.getTemporalPropositionDefinition(id);
                if (result == null) {
                    PropositionDefinition r
                            = propositionDefinitionCache.getPropositionDefinition(id);
                    if (r instanceof TemporalPropositionDefinition) {
                        try {
                            propositionDefinitionCache
                                    .addTemporalPropositionDefinition((TemporalPropositionDefinition) r);
                        } catch (InvalidPropositionIdException ex) {
                            throw new AssertionError("Should not happen");
                        }
                        result = (TemporalPropositionDefinition) r;
                    }
                }
                return result;
            }
        }

        @Override
        protected TemporalPropositionDefinition readFromBackend(String id, KnowledgeSourceBackend backend) throws KnowledgeSourceReadException {
            return backend.readTemporalPropositionDefinition(id);
        }

        @Override
        protected List<TemporalPropositionDefinition> readFromBackend(List<String> ids, KnowledgeSourceBackend backend) throws KnowledgeSourceReadException {
            return backend.readTemporalPropositionDefinitions(ids.toArray(new String[ids.size()]));
        }

        @Override
        protected void putInKnowledgeBase(TemporalPropositionDefinition propDef) throws InvalidPropositionIdException {
            synchronized (propositionDefinitionCache) {
                propositionDefinitionCache.addTemporalPropositionDefinition(propDef);
                propositionDefinitionCache.addPropositionDefinition(propDef);
            }
        }
    }

    /**
     * Returns the specified proposition definition.
     *
     * @param id a proposition id {@link String}. Cannot be <code>null</code>.
     * @return a {@link PropositionDefinition}, or <code>null</code> if none was
     * found with the given <code>id</code>.
     * @throws KnowledgeSourceReadException if an error occurred reading from
     * the knowledge base.
     */
    @Override
    public PropositionDefinition readPropositionDefinition(String id)
            throws KnowledgeSourceReadException {
        return this.propDefReader.read(id);
    }

    @Override
    public List<PropositionDefinition> readPropositionDefinitions(String... propIds)
            throws KnowledgeSourceReadException {
        return this.propDefReader.read(propIds);
    }

    @Override
    public boolean hasPropositionDefinition(String id)
            throws KnowledgeSourceReadException {
        return this.propDefReader.has(id);
    }

    @Override
    public AbstractionDefinition readAbstractionDefinition(String id) throws KnowledgeSourceReadException {
        return this.abstractionDefReader.read(id);
    }

    @Override
    public List<AbstractionDefinition> readAbstractionDefinitions(String... propIds) throws KnowledgeSourceReadException {
        return this.abstractionDefReader.read(propIds);
    }

    @Override
    public ContextDefinition readContextDefinition(String id) throws KnowledgeSourceReadException {
        return this.contextDefReader.read(id);
    }

    @Override
    public List<ContextDefinition> readContextDefinitions(String... propIds) throws KnowledgeSourceReadException {
        return this.contextDefReader.read(propIds);
    }

    @Override
    public boolean hasAbstractionDefinition(String id) throws KnowledgeSourceReadException {
        return this.abstractionDefReader.has(id);
    }

    @Override
    public boolean hasContextDefinition(String id) throws KnowledgeSourceReadException {
        return this.contextDefReader.has(id);
    }

    @Override
    public ValueSet readValueSet(String id) throws KnowledgeSourceReadException {
        ValueSet result = null;
        synchronized (this.notFoundValueSetRequests) {
            if (!this.notFoundValueSetRequests.containsKey(id)) {
                if (propositionDefinitionCache != null) {
                    synchronized (propositionDefinitionCache) {
                        result = propositionDefinitionCache.getValueSet(id);
                        if (result == null) {
                            initializeIfNeeded("reading the value set {0}", id);
                            if (getBackends() != null) {
                                for (KnowledgeSourceBackend backend : getBackends()) {
                                    result = backend.readValueSet(id);
                                    if (result != null) {
                                        try {
                                            this.propositionDefinitionCache.addValueSet(result);
                                        } catch (InvalidValueSetDefinitionException ex) {
                                            ProtempaUtil.logger().log(Level.SEVERE, "Error adding value set definition to cache", ex);
                                            throw new AssertionError("Error adding value set definition to cache: " + ex.getMessage());
                                        }
                                        return result;
                                    }
                                }
                                this.notFoundValueSetRequests.put(id, null);
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    @Override
    public boolean hasValueSet(String id) throws KnowledgeSourceReadException {
        boolean result = false;
        synchronized (this.notFoundValueSetRequests) {
            if (!this.notFoundValueSetRequests.containsKey(id)) {
                if (propositionDefinitionCache != null) {
                    synchronized (propositionDefinitionCache) {
                        result = propositionDefinitionCache.getValueSet(id) != null;
                    }
                }
                if (!result) {
                    initializeIfNeeded("reading the value set {0}", id);
                    for (KnowledgeSourceBackend backend : getBackends()) {
                        result = backend.readValueSet(id) != null;
                        if (result) {
                            return result;
                        }
                    }
                    this.notFoundValueSetRequests.put(id, null);
                }
            }
        }
        return result;
    }

    @Override
    public void close() throws SourceCloseException {
        clear();
        this.propositionDefinitionCache = null;
        super.close();
    }

    @Override
    public void clear() {
        if (this.propositionDefinitionCache != null) {
            synchronized (this.propositionDefinitionCache) {
                this.propositionDefinitionCache.clear();
            }
            synchronized (this.inverseIsACache) {
                this.inverseIsACache.clear();
            }
            synchronized (this.abstractedFromCache) {
                this.abstractedFromCache.clear();
            }
            synchronized (this.subContextsCache) {
                this.subContextsCache.clear();
            }
            synchronized (this.inducedByCache) {
                this.inducedByCache.clear();
            }
            synchronized (this.abstractionDefReader) {
                this.notFoundAbstractionDefinitionRequests.clear();
            }
            synchronized (this.notFoundValueSetRequests) {
                this.notFoundValueSetRequests.clear();
            }
            synchronized (this.propDefReader) {
                this.notFoundPropositionDefinitionRequests.clear();
            }
            this.inDataSourceGetter.clear();
            this.collectSubtreeGetter.clear();
        }
    }

    @Override
    public Set<String> collectPropIdDescendantsUsingAllNarrower(boolean inDataSourceOnly, String... propIds)
            throws KnowledgeSourceReadException {
        initializeIfNeeded(
                "Getting proposition ids for {0} with inDataSource set to true",
                StringUtils.join(propIds, ","));
        ProtempaUtil.checkArrayForNullElement(propIds, "propIds");
        return this.inDataSourceGetter.subtreePropIds(inDataSourceOnly, propIds);
    }

    @Override
    public Set<PropositionDefinition> collectPropDefDescendantsUsingAllNarrower(
            boolean inDataSourceOnly, String... propIds) throws KnowledgeSourceReadException {
        initializeIfNeeded(
                "Getting proposition definitions for {0} with inDataSource set to true",
                StringUtils.join(propIds, ","));
        ProtempaUtil.checkArrayForNullElement(propIds, "propIds");
        return this.inDataSourceGetter.subtreePropDefs(inDataSourceOnly, propIds);
    }

    @Override
    public Set<String> collectPropIdDescendantsUsingInverseIsA(String... propIds) throws KnowledgeSourceReadException {
        initializeIfNeeded(
                "Getting proposition ids for {0}",
                StringUtils.join(propIds, ","));
        ProtempaUtil.checkArrayForNullElement(propIds, "propIds");
        return this.collectSubtreeGetter.subtreePropIds(false, propIds);
    }

    @Override
    public Set<PropositionDefinition> collectPropDefDescendantsUsingInverseIsA(String... propIds) throws KnowledgeSourceReadException {
        initializeIfNeeded(
                "Getting proposition definitions for {0}",
                StringUtils.join(propIds, ","));
        ProtempaUtil.checkArrayForNullElement(propIds, "propIds");
        return this.collectSubtreeGetter.subtreePropDefs(false, propIds);
    }

    /**
     * Gets the mappings from term IDs to proposition IDs for each backend.
     *
     * @return a {@link Map} of {@link String}s to a {@link List} of
     * <code>String</code>s, with the keys being {@link Term} IDs and the values
     * being lists of {@link PropositionDefinition} IDs.
     */
    @Override
    public List<String> getPropositionDefinitionsByTerm(
            And<TermSubsumption> termSubsumptionClause)
            throws KnowledgeSourceReadException {
        List<String> result = new ArrayList<>();
        initializeIfNeeded("getting proposition definitions by term");
        for (KnowledgeSourceBackend backend : getBackends()) {
            result.addAll(backend.getPropositionsByTermSubsumption(termSubsumptionClause));
        }

        return result;
    }

    @Override
    public void backendUpdated(KnowledgeSourceBackendUpdatedEvent event) {
        clear();
        fireKnowledgeSourceUpdated();
    }

    /**
     * Notifies registered listeners that the knowledge source has been updated.
     *
     * @see KnowledgeSourceUpdatedEvent
     * @see SourceListener
     */
    private void fireKnowledgeSourceUpdated() {
        fireSourceUpdated(new KnowledgeSourceUpdatedEvent(this));
    }

    /**
     * Gets the proposition Definitions from each backend that match the
     * searchKey
     *
     * @param searchKey
     * @return
     * @throws KnowledgeSourceReadException
     */
    @Override
    public List<String> getMatchingPropIds(String searchKey)
            throws KnowledgeSourceReadException {
        ProtempaUtil.logger().log(Level.INFO,
                "Searching knowledge source For search string {0}", searchKey);
        Set<String> searchResults = getSearchResultsFromBackend(searchKey);
        return new ArrayList<>(searchResults);
    }

    private Set<String> getSearchResultsFromBackend(String searchKey)
            throws KnowledgeSourceReadException {
        Set<String> results = new HashSet<>();
        for (KnowledgeSourceBackend backend : getBackends()) {
            results.addAll(backend.getKnowledgeSourceSearchResults(searchKey));
        }
        return results;
    }
}
