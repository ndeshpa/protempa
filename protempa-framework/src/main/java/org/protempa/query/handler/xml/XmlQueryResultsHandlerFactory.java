package org.protempa.query.handler.xml;

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

import java.io.Writer;
import java.util.Map;
import org.protempa.KnowledgeSource;
import org.protempa.query.Query;
import org.protempa.query.handler.CollectStatisticsException;
import org.protempa.query.handler.QueryResultsHandler;
import org.protempa.query.handler.QueryResultsHandlerFactory;
import org.protempa.query.handler.QueryResultsHandlerInitException;
import org.protempa.query.handler.StatisticsCollector;
import org.protempa.query.handler.StatisticsCollectorInitException;

/**
 *
 * @author Andrew Post
 */
public class XmlQueryResultsHandlerFactory implements QueryResultsHandlerFactory {
    private final boolean inferPropositionIdsNeeded;
    private final String[] propIds;
    private final String initialPropId;
    private final Map<String, String> propOrder;
    private final Writer writer;
    
    /**
     * Constructor
     *
     * @param writer The {@link Writer} object that will be used to write the
     * output.
     * @param propOrder A {@link Map} object that will be used to guide the
     * navigation between some propositions. If this results handler visits a
     * proposition, the ID of the proposition equals a key in the Map and the
     * value associated with the key is a name in a {@link Reference} associated
     * with the proposition, then the Reference is used to navigate from the
     * proposition. This has the effect of imposing a partial ordering on the
     * nesting of XML elements in the output.
     * @param initialPropId The ID of the proposition that will be the root of
     * the output.
     * @param propIds The IDs of propositions that are to be included in the
     * output. If a proposition's ID is not included in this array, it will not
     * be included in the output.
     */
    public XmlQueryResultsHandlerFactory(Writer writer, Map<String, String> propOrder,
            String initialPropId, String[] propIds) {
        this(writer, propOrder, initialPropId, propIds, true);
    }
    
    public XmlQueryResultsHandlerFactory(Writer writer, Map<String, String> propOrder,
            String initialPropId, String[] propIds,
            boolean inferPropositionIdsNeeded) {
        this.writer = writer;
        this.propOrder = propOrder;
        this.initialPropId = initialPropId;
        if (propIds == null) {
            this.propIds = new String[0];
        } else {
            this.propIds = propIds.clone();
        }
        this.inferPropositionIdsNeeded = inferPropositionIdsNeeded;
    }

    @Override
    public QueryResultsHandler getInstance(Query query, KnowledgeSource knowledgeSource) throws QueryResultsHandlerInitException {
        return new XmlQueryResultsHandler(this.writer, this.propOrder, this.initialPropId, this.propIds, this.inferPropositionIdsNeeded, knowledgeSource);
    }

    @Override
    public StatisticsCollector getStatisticsCollector() throws StatisticsCollectorInitException {
        return null;
    }

    
    
}
