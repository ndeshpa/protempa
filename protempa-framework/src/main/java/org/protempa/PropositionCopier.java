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

import java.io.Serializable;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.drools.WorkingMemory;
import org.drools.spi.KnowledgeHelper;
import org.protempa.proposition.AbstractParameter;
import org.protempa.proposition.visitor.AbstractPropositionVisitor;
import org.protempa.proposition.Constant;
import org.protempa.proposition.Context;
import org.protempa.proposition.Event;
import org.protempa.proposition.PrimitiveParameter;
import org.protempa.proposition.ProviderBasedUniqueIdFactory;
import org.protempa.proposition.UniqueIdFactory;

/**
 * Creates derived propositions when discovered by PROTEMPA that have the same
 * characteristics as the proposition from which it was derived 
 * (e.g., interval, value). Users
 * should create one instance of this class per proposition to derive. 
 * Whenever a proposition with that id is to be created, the user should first 
 * call the corresponding copier's
 * {@link #grab(org.drools.WorkingMemory)} method with the current
 * {@link WorkingMemory} instance, call 
 * {@link org.protempa.proposition.Proposition#accept(org.protempa.proposition.PropositionVisitor)}
 * with the copier as the argument, and then call the copier's 
 * {@link #release()} method. NOTE: failing to call {@link #release()} will 
 * cause a memory leak!
 * 
 * @author Andrew Post
 */
class PropositionCopier extends AbstractPropositionVisitor implements Serializable {

    private static final long serialVersionUID = 1236050515546951710L;
    private static final Logger LOGGER = Logger.getLogger(PropositionCopier.class.getName());
    
    private final String propId;
    private final DerivationsBuilder derivationsBuilder;
    private KnowledgeHelper kh;
    private UniqueIdFactory uniqueIdProvider;

    /**
     * Instances a copier with the id of the proposition to derive, and a
     * {@link DerivationsBuilder} to store links between the source and derived
     * propositions.
     * 
     * @param propId the id {@link String} of the proposition to derive.
     * Cannot be <code>null</code>.
     * @param derivationsBuilder a {@link DerivationsBuilder}. Cannot be
     * <code>null</code>.
     */
    PropositionCopier(String propId, DerivationsBuilder derivationsBuilder) {
        assert propId != null : "propId cannot be null";
        assert derivationsBuilder != null : 
                "derivationsBuilder cannot be null";
        this.derivationsBuilder = derivationsBuilder;
        this.propId = propId;
    }

    /**
     * Grabs this copier for use by a Drools consequence.
     * 
     * @param workingMemory the consequence's {@link WorkingMemory}. Cannot
     * be <code>null</code>.
     */
    void grab(KnowledgeHelper kh) {
        assert kh != null : "kh cannot be null";
        assert this.kh == null : "The previous user of this copier forgot to call release!";
        if (this.kh != null) {
            LOGGER.log(Level.WARNING, 
                    "The previous user of this copier forgot to call release. This causes a memory leak!");
        }
        this.kh = kh;
        this.uniqueIdProvider = 
                new ProviderBasedUniqueIdFactory(new JBossRulesDerivedLocalUniqueIdValuesProvider(this.kh.getWorkingMemory(), 
                        this.propId));
    }
    
    /**
     * Releases this copier for use by another Drools consequence.
     */
    void release() {
        this.kh = null;
    }

    /**
     * Creates a derived abstract parameter with the id specified in the
     * constructor and the same characteristics (e.g., data source type,
     * interval, value, etc.).
     * 
     * @param abstractParameter an {@link AbstractParameter}. Cannot be 
     * <code>null</code>.
     */
    @Override
    public void visit(AbstractParameter abstractParameter) {
        assert this.kh != null : "kh wasn't set";
        AbstractParameter param = new AbstractParameter(propId, this.uniqueIdProvider.getInstance());
        param.setSourceSystem(SourceSystem.DERIVED);
        param.setInterval(abstractParameter.getInterval());
        param.setValue(abstractParameter.getValue());
        param.setCreateDate(new Date());
        this.kh.insertLogical(param);
        this.derivationsBuilder.propositionAsserted(abstractParameter, param);
        LOGGER.log(Level.FINER, "Asserted derived proposition {0}", param);
    }

    /**
     * Creates a derived event with the id specified in the
     * constructor and the same characteristics (e.g., data source type,
     * interval, etc.).
     * 
     * @param event an {@link Event}. Cannot be <code>null</code>.
     */
    @Override
    public void visit(Event event) {
        assert this.kh != null : "kh wasn't set";
        Event e = new Event(propId, this.uniqueIdProvider.getInstance());
        e.setInterval(event.getInterval());
        e.setSourceSystem(SourceSystem.DERIVED);
        e.setCreateDate(new Date());
        this.kh.insertLogical(e);
        this.derivationsBuilder.propositionAsserted(event, e);
        LOGGER.log(Level.FINER, "Asserted derived proposition {0}", e);
    }

    /**
     * Creates a derived primitive parameter with the id specified in the
     * constructor and the same characteristics (e.g., data source type,
     * interval, value, etc.).
     * 
     * @param primitiveParameter a {@link PrimitiveParameter}. Cannot be 
     * <code>null</code>.
     */
    @Override
    public void visit(PrimitiveParameter primitiveParameter) {
        assert this.kh != null : "kh wasn't set";
        PrimitiveParameter param = new PrimitiveParameter(propId, this.uniqueIdProvider.getInstance());
        param.setPosition(primitiveParameter.getPosition());
        param.setGranularity(primitiveParameter.getGranularity());
        param.setValue(primitiveParameter.getValue());
        param.setSourceSystem(SourceSystem.DERIVED);
        param.setCreateDate(new Date());
        this.kh.insertLogical(param);
        this.derivationsBuilder.propositionAsserted(primitiveParameter, param);
        LOGGER.log(Level.FINER, "Asserted derived proposition {0}", param);
    }

    /**
     * Creates a derived constant with the id specified in the
     * constructor and the same characteristics (e.g., data source type,
     * interval, value, etc.).
     * 
     * @param constant a {@link Constant}. Cannot be <code>null</code>.
     */
    @Override
    public void visit(Constant constant) {
        assert this.kh != null : "kh wasn't set";
        Constant newConstant = new Constant(propId, this.uniqueIdProvider.getInstance());
        newConstant.setSourceSystem(SourceSystem.DERIVED);
        newConstant.setCreateDate(new Date());
        this.kh.insertLogical(newConstant);
        this.derivationsBuilder.propositionAsserted(constant, newConstant);
        LOGGER.log(Level.FINER, "Asserted derived proposition {0}", newConstant);
    }



    /**
     * Unsupported operation!
     * 
     * @param context 
     */
    @Override
    public void visit(Context context) {
        assert this.kh != null : "kh wasn't set";
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
