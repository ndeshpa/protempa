package org.protempa;

/*
 * #%L
 * Protempa Framework
 * %%
 * Copyright (C) 2012 - 2015 Emory University
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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import org.arp.javautil.arrays.Arrays;

/**
 *
 * @author Andrew Post
 */
class CollectSubtreeGetterSlowStrategy {

    private final Map<String, PropositionDefinition> propositionDefinitionMap;
    private final PropositionDefinitionWalker propDefWalker;
    private final PropIdWalker propIdWalker;
    private final boolean narrower;

    CollectSubtreeGetterSlowStrategy(Map<String, PropositionDefinition> propositionDefinitionMap, boolean narrower) {
        assert propositionDefinitionMap != null : "propositionDefinitionMap cannot be null";
        this.propositionDefinitionMap = propositionDefinitionMap;
        this.narrower = narrower;
        this.propDefWalker = new PropositionDefinitionWalker();
        this.propIdWalker = new PropIdWalker();
    }

    class InDataSourceResult<E> {

        private final Set<E> result;
        private final Set<String> missing;

        private InDataSourceResult(Set<E> result, Set<String> missing) {
            this.result = result;
            this.missing = missing;
        }

        public Set<E> getResult() {
            return result;
        }

        public Set<String> getMissing() {
            return missing;
        }

    }

    InDataSourceResult<String> collectPropIds(boolean inDataSourceOnly, Set<String> propIds)
            throws KnowledgeSourceReadException {
        if (propIds.contains(null)) {
            throw new IllegalArgumentException(
                    "propIds cannot contain a null element");
        }
        return this.propIdWalker.walkNarrowerPropDefs(inDataSourceOnly, propIds);
    }

    InDataSourceResult<PropositionDefinition> collectPropDefs(boolean inDataSourceOnly, Set<String> propIds)
            throws KnowledgeSourceReadException {
        if (propIds.contains(null)) {
            throw new IllegalArgumentException(
                    "propIds cannot contain a null element");
        }
        return this.propDefWalker.walkNarrowerPropDefs(inDataSourceOnly, propIds);
    }

    private abstract class Walker<E> {

        InDataSourceResult<E> walkNarrowerPropDefs(boolean inDataSource, Set<String> propIds) {
            Set<E> found = new HashSet<>();
            Set<String> missing = new HashSet<>();
            Queue<String> queue = new LinkedList<>(propIds);
            while (!queue.isEmpty()) {
                String propId = queue.poll();
                PropositionDefinition pd = propositionDefinitionMap.get(propId);
                if (pd != null) {
                    if (!inDataSource || pd.getInDataSource()) {
                        addToFound(propId, pd, found);
                    }
                    if (narrower) {
                        Arrays.addAll(queue, pd.getChildren());
                    } else {
                        Arrays.addAll(queue, pd.getInverseIsA());
                    }
                } else {
                    missing.add(propId);
                }
            }
            InDataSourceResult<E> inDataSourceResult = new InDataSourceResult<>(found, missing);
            return inDataSourceResult;
        }

        protected abstract void addToFound(String propId, PropositionDefinition propDef, Set<E> found);
    }

    private final class PropositionDefinitionWalker extends Walker<PropositionDefinition> {

        @Override
        protected void addToFound(String propId, PropositionDefinition propDef, Set<PropositionDefinition> found) {
            found.add(propDef);
        }

    }

    private final class PropIdWalker extends Walker<String> {

        @Override
        protected void addToFound(String propId, PropositionDefinition propDef, Set<String> found) {
            found.add(propId);
        }
    }

}
