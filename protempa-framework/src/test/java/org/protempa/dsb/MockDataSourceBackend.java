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
package org.protempa.dsb;

import org.protempa.DataSourceReadException;
import org.protempa.KnowledgeSource;
import org.protempa.KnowledgeSourceReadException;
import org.protempa.MultiplexingDataStreamingEventIterator;
import org.protempa.backend.BackendInitializationException;
import org.protempa.backend.BackendInstanceSpec;
import org.protempa.backend.DataSourceBackendFailedConfigurationValidationException;
import org.protempa.backend.DataSourceBackendFailedDataValidationException;
import org.protempa.backend.annotations.BackendInfo;
import org.protempa.backend.dsb.AbstractDataSourceBackend;
import org.protempa.backend.dsb.DataValidationEvent;
import org.protempa.backend.dsb.filter.Filter;
import org.protempa.proposition.value.GranularityFactory;
import org.protempa.proposition.value.UnitFactory;

import java.util.Set;
import org.protempa.dest.QueryResultsHandler;


/**
 *
 * @author Andrew Post
 */
@BackendInfo(displayName = "Mock Data Source Backend")
public class MockDataSourceBackend extends AbstractDataSourceBackend {

    @Override
    public void initialize(BackendInstanceSpec config)
            throws BackendInitializationException {
//        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public GranularityFactory getGranularityFactory() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public UnitFactory getUnitFactory() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getKeyType() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getDisplayName() {
        return "Mock Data Source Backend";
    }

    @Override
    public DataValidationEvent[] validateData(KnowledgeSource knowledgeSource) throws DataSourceBackendFailedDataValidationException, KnowledgeSourceReadException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public void validateConfiguration(KnowledgeSource knowledgeSource)
            throws DataSourceBackendFailedConfigurationValidationException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public MultiplexingDataStreamingEventIterator readPropositions(Set<String> keyIds, Set<String> propIds, Filter filters, QueryResultsHandler queryResultsHandler) throws DataSourceReadException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
