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
package org.protempa.proposition.stats;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.protempa.SourceSystem;
import org.protempa.DataSourceBackendSourceSystem;
import org.protempa.ProtempaTestCase;
import org.protempa.proposition.AbstractParameter;
import org.protempa.proposition.Event;
import org.protempa.proposition.PrimitiveParameter;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.TemporalAbstractParameterFactory;
import org.protempa.proposition.TemporalEventFactory;
import org.protempa.proposition.TemporalPrimitiveParameterFactory;
import org.protempa.proposition.value.AbsoluteTimeGranularity;
import org.protempa.proposition.value.AbsoluteTimeUnit;
import org.protempa.proposition.value.NumberValue;

public class MinLengthKaplanMeyerTest extends ProtempaTestCase {

    private List<Proposition> propositions;
    private MinLengthKaplanMeyer mls;
    private SourceSystem dbDataSourceType;
    private SourceSystem derivedDataSourceType;

    @Override
    protected void setUp() throws Exception {
        this.propositions = new ArrayList<>();
        this.dbDataSourceType
                = DataSourceBackendSourceSystem.getInstance("MockTestDatabase");
        this.derivedDataSourceType = SourceSystem.DERIVED;
        DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);

        TemporalPrimitiveParameterFactory tppf = new TemporalPrimitiveParameterFactory(
                df, AbsoluteTimeGranularity.DAY, getUniqueIdFactory());

        PrimitiveParameter pp = tppf.getInstance("1", "1/1/07",
                new NumberValue(2), dbDataSourceType);
        this.propositions.add(pp);

        TemporalEventFactory tef = new TemporalEventFactory(df,
                AbsoluteTimeGranularity.DAY,
                getUniqueIdFactory());

        Event event = tef.getInstance("2", "1/1/07", "2/1/07", derivedDataSourceType);
        Event event2 = tef.getInstance("2", "8/15/96", "9/12/96", derivedDataSourceType);
        Event event3 = tef.getInstance("2", "2/1/05", "3/2/05", derivedDataSourceType);
        Event event4 = tef.getInstance("2", "2/1/05", "1/20/06", derivedDataSourceType);
        this.propositions.add(event);
        this.propositions.add(event2);
        this.propositions.add(event3);
        this.propositions.add(event4);

        TemporalAbstractParameterFactory tapf = new TemporalAbstractParameterFactory(
                df, AbsoluteTimeGranularity.DAY, getUniqueIdFactory());

        AbstractParameter ap = tapf.getInstance("3", "1/1/72", "1/2/72");
        AbstractParameter ap2 = tapf.getInstance("3", "4/2/81", "4/1/82");
        AbstractParameter ap3 = tapf.getInstance("3", "4/2/81", "3/30/82");

        this.propositions.add(ap);
        this.propositions.add(ap2);
        this.propositions.add(ap3);

        this.mls = new MinLengthKaplanMeyer(AbsoluteTimeUnit.DAY);
        this.mls.visit(this.propositions);
    }

    @Override
    protected void tearDown() throws Exception {
        this.propositions = null;
        this.mls = null;
    }

    public void testPrimitiveParameter1NoValue() {
        assertEquals(null, this.mls.get("1"));
    }

    public void testPrimitiveParameter1WithValue() {
        Map<Long, Integer> expected = new HashMap<>();
        expected.put(0L, 1);
        assertEquals(expected, this.mls.get("1", new NumberValue(2)));
    }

    public void testEvent2() {
        Map<Long, Integer> expected = new HashMap<>();
        expected.put(28L, 1);
        expected.put(31L, 1);
        expected.put(29L, 1);
        expected.put(353L, 1);
        assertEquals(expected, this.mls.get("2"));
    }

    public void testAbstractParameter3() {
        Map<Long, Integer> expected = new HashMap<>();
        expected.put(364L, 1);
        expected.put(1L, 1);
        expected.put(362L, 1);
        assertEquals(expected, this.mls.get("3"));
    }

}
