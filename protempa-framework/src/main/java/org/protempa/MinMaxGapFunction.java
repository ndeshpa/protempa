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

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.protempa.proposition.interval.Interval;
import org.protempa.proposition.interval.Relation;
import org.protempa.proposition.value.Unit;

/**
 * @author Andrew Post
 */
public final class MinMaxGapFunction extends GapFunction {

    private static final long serialVersionUID = -7646406614416920314L;
    private Integer minimumGap = 0;
    private Unit minimumGapUnits;
    private Integer maximumGap;
    private Unit maximumGapUnits;
    private Relation relation;

    /**
     * Instantiates an instance with the default minimum and maximum gap and
     * units.
     */
    public MinMaxGapFunction() {
        this(null, null, null, null);
    }

    public MinMaxGapFunction(Integer minimumGap, Unit minimumGapUnit,
            Integer maximumGap, Unit maximumGapUnit) {
        this.minimumGapUnits = minimumGapUnit;
        setMinimumGap(minimumGap);
        this.maximumGapUnits = maximumGapUnit;
        setMaximumGap(maximumGap);
    }

    @Override
    public boolean execute(Interval lhs, Interval rhs) {
        return this.relation.hasRelation(lhs, rhs);
    }

    public Integer getMinimumGap() {
        return this.minimumGap;
    }

    public Unit getMinimumGapUnit() {
        return this.minimumGapUnits;
    }

    public Integer getMaximumGap() {
        return this.maximumGap;
    }

    public Unit getMaximumGapUnit() {
        return this.maximumGapUnits;
    }

    public void setMaximumGap(Integer maximumGap) {
        if (maximumGap != null && maximumGap < 0) {
            maximumGap = 0;
        }
        this.maximumGap = maximumGap;
        setRelation();
    }

    public void setMinimumGap(Integer minimumGap) {
        if (minimumGap == null || minimumGap < 0) {
            minimumGap = 0;
        }
        this.minimumGap = minimumGap;
        setRelation();
    }

    public void setMaximumGapUnit(Unit unit) {
        this.maximumGapUnits = unit;
        setRelation();
    }

    public void setMinimumGapUnit(Unit unit) {
        this.minimumGapUnits = unit;
        setRelation();
    }

    private void setRelation() {
        this.relation = new Relation(null, null, null, null, null, null, null,
                null, this.minimumGap, this.minimumGapUnits, this.maximumGap,
                this.maximumGapUnits, null, null, null, null);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .appendSuper(super.toString())
                .append("minimumGap", this.minimumGap)
                .append("minimumGapUnits", this.minimumGapUnits)
                .append("maximumGap", this.maximumGap)
                .append("maximumGapUnits", this.maximumGapUnits)
                .toString();
    }


}
