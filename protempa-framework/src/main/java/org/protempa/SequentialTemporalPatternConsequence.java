/*
 * #%L
 * Protempa Framework
 * %%
 * Copyright (C) 2012 Emory University
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.arp.javautil.arrays.Arrays;

import org.drools.WorkingMemory;
import org.drools.spi.Consequence;
import org.drools.spi.KnowledgeHelper;
import org.drools.spi.Tuple;
import org.protempa.SequentialTemporalPatternDefinition.RelatedTemporalExtendedPropositionDefinition;
import org.protempa.proposition.AbstractParameter;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.Segment;
import org.protempa.proposition.Sequence;
import org.protempa.proposition.TemporalProposition;
import org.protempa.proposition.interval.Relation;

/**
 * @author Andrew Post
 */
class SequentialTemporalPatternConsequence implements Consequence {

    private static final long serialVersionUID = -833609244124008166L;
    private final SequentialTemporalPatternDefinition def;
    private final int columns;
    private final TemporalExtendedPropositionDefinition[] epds;
    private final DerivationsBuilder derivationsBuilder;
    private int parameterMapCapacity;
    private List<List<TemporalExtendedPropositionDefinition>> epdPairs;
    private Map<List<TemporalExtendedPropositionDefinition>, Relation> epdToRelation;

    /**
     *
     * @param def a {@link HighLevelAbstractionDefinition}, cannot be
     * <code>null</code>.
     * @param columns the number of parameters, must be greater than zero.
     */
    SequentialTemporalPatternConsequence(SequentialTemporalPatternDefinition def,
            DerivationsBuilder derivationsBuilder) {
        assert def != null : "def cannot be null";
        this.def = def;
        RelatedTemporalExtendedPropositionDefinition[] relatedTemporalExtendedPropositionDefinitions = def.getRelatedTemporalExtendedPropositionDefinitions();
        TemporalExtendedPropositionDefinition[] epds = new TemporalExtendedPropositionDefinition[relatedTemporalExtendedPropositionDefinitions.length + 1];
        assert epds != null : "epds cannot be null";
        int col = epds.length;
        assert col > 0 : "columns must be > 0, was " + col;
        epds[0] = def.getMainTemporalExtendedPropositionDefinition();
        for (int i = 1; i < epds.length; i++) {
            epds[i] = relatedTemporalExtendedPropositionDefinitions[i - 1].getRelatedTemporalExtendedPropositionDefinition();
        }
        this.epds = epds;
        this.columns = col;
        this.derivationsBuilder = derivationsBuilder;
        this.parameterMapCapacity = this.epds.length * 4 / 3 + 1;
        this.epdPairs = new ArrayList<List<TemporalExtendedPropositionDefinition>>();
        this.epdToRelation = new HashMap<List<TemporalExtendedPropositionDefinition>, Relation>(
                this.parameterMapCapacity);
        TemporalExtendedPropositionDefinition lhs = def.getMainTemporalExtendedPropositionDefinition();
        assert lhs != null : "mainTemporalExtendedPropositionDefinition cannot be null";
        for (RelatedTemporalExtendedPropositionDefinition rhsr :
                def.getRelatedTemporalExtendedPropositionDefinitions()) {
            TemporalExtendedPropositionDefinition rhs = rhsr.getRelatedTemporalExtendedPropositionDefinition();
            List<TemporalExtendedPropositionDefinition> asList =
                    Arrays.asList(
                    new TemporalExtendedPropositionDefinition[]{
                        lhs,
                        rhs
                    });
            this.epdPairs.add(asList);
            this.epdToRelation.put(asList, rhsr.getRelation());
            lhs = rhs;
        }
    }

    @Override
    public void evaluate(KnowledgeHelper knowledgeHelper, WorkingMemory arg1)
            throws Exception {
        Logger logger = ProtempaUtil.logger();
        @SuppressWarnings("unchecked")
        List<TemporalProposition> tps = (List<TemporalProposition>) knowledgeHelper
                .get(knowledgeHelper.getDeclaration("result"));
        Collections.sort(tps, ProtempaUtil.TEMP_PROP_COMP);

        TOP_LEVEL:
        for (int i = 0, l = this.epds.length, n = tps.size() - l + 1; i < n; i++) {
            List<TemporalProposition> subList = tps.subList(i, i + l);

            /*
             * For constructing a map of extended proposition definition to actual
             * temporal proposition.
             */
            Map<TemporalExtendedPropositionDefinition, TemporalProposition> propositionMap =
                    new HashMap<TemporalExtendedPropositionDefinition, TemporalProposition>(this.parameterMapCapacity);
            /*
             * Populate the map.
             */
            for (int j = 0; j < l; j++) {
                TemporalProposition p = subList.get(j);
                if (!p.getId().equals(epds[j].getPropositionId())) {
                    continue TOP_LEVEL;
                }
                propositionMap.put(epds[j], p);
            }

            /*
             * Check for the presence of the specified temporal relations.
             */
            if (HighLevelAbstractionFinder.find(this.epdToRelation,
                    this.epdPairs, propositionMap)) {
                Segment<TemporalProposition> segment =
                        new Segment<TemporalProposition>(
                        new Sequence<TemporalProposition>(def.getPropositionId(), subList));
                TemporalPatternOffset temporalOffset = def.getTemporalOffset();
                AbstractParameter result =
                        AbstractParameterFactory.getFromAbstraction(def.getPropositionId(),
                        segment, subList, null, temporalOffset, epds, null);
                knowledgeHelper.getWorkingMemory().insert(result);
                for (Proposition proposition : segment) {
                    this.derivationsBuilder.propositionAsserted(proposition, result);
                }
                logger.log(Level.FINER, "Asserted derived proposition {0}", result);
            }
        }
    }
}
