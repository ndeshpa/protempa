/*
 * #%L
 * Protempa Commons Backend Provider
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
package org.protempa.backend.dsb.relationaldb.mysql;

import java.util.LinkedHashMap;
import org.protempa.backend.dsb.filter.Filter;

import java.util.List;
import java.util.Set;
import org.arp.javautil.sql.DatabaseVersion;
import org.arp.javautil.sql.DriverVersion;
import org.protempa.backend.dsb.relationaldb.AbstractSQLGeneratorWithCompatChecks;
import org.protempa.backend.dsb.relationaldb.EntitySpec;
import org.protempa.backend.dsb.relationaldb.ReferenceSpec;
import org.protempa.backend.dsb.relationaldb.SQLGenResultProcessor;
import org.protempa.backend.dsb.relationaldb.SQLOrderBy;
import org.protempa.backend.dsb.relationaldb.SelectStatement;

/**
 * A SQL generator that is compatible with Connector/J 5.x and MySQL 4.1 and
 * 5.x.
 *
 * @author Andrew Post
 */
public class ConnectorJ5MySQL415Generator extends AbstractSQLGeneratorWithCompatChecks {
    
    private static final String DRIVER_CLASS_NAME = "com.mysql.jdbc.Driver";
    private static final String DRIVER_NAME = "MySQL-AB JDBC Driver";
    private static final DriverVersion MIN_DRIVER_VERSION = new DriverVersion(5, 0);
    private static final DriverVersion MAX_DRIVER_VERSION = new DriverVersion(5, Integer.MAX_VALUE);
    private static final String DATABASE_PRODUCT_NAME = "MySQL";
    private static final DatabaseVersion MIN_DATABASE_VERSION = new DatabaseVersion(4, 1);
    private static final DatabaseVersion MAX_DATABASE_VERSION = new DatabaseVersion(5, Integer.MAX_VALUE);

    public ConnectorJ5MySQL415Generator() {
        super(DRIVER_CLASS_NAME,
                DRIVER_NAME,
                MIN_DRIVER_VERSION, MAX_DRIVER_VERSION,
                DATABASE_PRODUCT_NAME,
                MIN_DATABASE_VERSION, MAX_DATABASE_VERSION);
    }
    
    @Override
    /*
     * MySQL SQL generator does not currently support staging data (non-Javadoc)
     * 
     * @see org.protempa.bp.commons.dsb.relationaldb.AbstractSQLGenerator#
     * getSelectStatement(org.protempa.bp.commons.dsb.relationaldb.EntitySpec,
     * org.protempa.bp.commons.dsb.relationaldb.ReferenceSpec, java.util.List,
     * java.util.Set, java.util.Set, java.util.Set,
     * org.protempa.bp.commons.dsb.relationaldb.SQLOrderBy,
     * org.protempa.bp.commons.dsb.relationaldb.SQLGenResultProcessor,
     * java.util.Map)
     */
    protected SelectStatement getSelectStatement(EntitySpec entitySpec,
            List<EntitySpec> entitySpecs,
            LinkedHashMap<String, ReferenceSpec> inboundRefSpecs,
            Set<Filter> filters, Set<String> propIds, Set<String> keyIds,
            SQLOrderBy order, SQLGenResultProcessor resultProcessor,
            boolean wrapKeyId) {
        return new ConnectorJ5MySQL415SelectStatement(entitySpec,
                entitySpecs, inboundRefSpecs, filters, propIds, keyIds, order,
                resultProcessor, wrapKeyId);
    }
}
