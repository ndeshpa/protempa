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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.protempa.proposition.value.NominalValue;
import org.protempa.proposition.value.Value;

/**
 * An abstraction for representing compound low-level abstractions. These are
 * essentially low-level abstractions composed of multiple data types and whose
 * values are computed based on combinations the values of the underlying
 * low-level abstractions in overlapping intervals.
 * 
 * Note that this is different than a {@link LowLevelAbstractionDefinition}
 * abstracted from multiple primitive parameters. In that case, the low-level
 * abstraction takes on a value based on the computation of all of the primitive
 * parameter values by a single algorithm. In compound low-level abstractions,
 * the values of the underlying low-level abstractions are considered
 * individually, and a higher-level value is assigned to an interval based on
 * rules.
 * 
 * A compound low-level abstraction will generate a superset of the intervals in
 * the abstracted-from low-level abstractions.
 * 
 * Just as for low-level abstractions, value definitions must be created for
 * compound low-level abstractions. These tell the abstraction how to process
 * the values of the underlying low-level abstraction. There are two ways to
 * consider the values of the low-level abstractions in a particular interval,
 * provided by the enum {@link ValueDefinitionMatchOperator}: ANY or ALL.
 * Specifying ANY will make the compound abstraction apply the value under
 * consideration if any of the low-level abstraction values in that interval
 * match the rule for that value. ALL requires all of the low-level abstraction
 * rules to match.
 * 
 * The rules look something like:
 * 
 * <code>COMPOUND_LLA_VALUE1 = {LLA1.value=LLA1_VALUE1,LLA2.value=LLA2_VALUE1,...,LLAN.value=LLAN_VALUE1}</code>
 * <code>COMPOUND_LLA_VALUE2 = {LLA1.value=LLA1_VALUE2,LLA2.value=LLA2_VALUE2,...,LLAN.value=LLAN_VALUE2}</code>
 * 
 * The ANY or ALL operator applies to all of the rules. It is not applied on a
 * per-rule basis.
 * 
 * Order matters when specifying the compound abstractions possible values.
 * Values will be considered in the order they were defined. The last value will
 * be considered as a catch-all if none of the others match. It should typically
 * be the "normal" interpretation of the underlying low-level abstractions.
 * 
 * See {@link org.protempa.test.ProtempaTest} in protempa-test-suite for
 * examples of compound low-level abstractions.
 */
public class CompoundLowLevelAbstractionDefinition extends
        AbstractAbstractionDefinition {

    private static final long serialVersionUID = -1285908778762502403L;

    private final Set<String> lowLevelIds;
    private final LinkedHashMap<String, Map<String, Value>> classificationMatrix;

    /*
     * Number of consecutive intervals with the same value required for this
     * abstraction to be asserted. Default is 1.
     */
    private int minimumNumberOfValues;

    /**
     * The different ways to consider the low-level abstraction value rules in a
     * given interval
     */
    public static enum ValueDefinitionMatchOperator {
        /**
         * Assert the value if any of the low-level abstractions have the
         * appropriate value
         */
        ANY,

        /**
         * Assert the value if all of the low-level abstractions have the
         * appropriate value
         */
        ALL
    }

    private ValueDefinitionMatchOperator valueDefinitionMatchOperator;

    private boolean concatenable;

    /**
     * Constructor for this class. Sets the proposition ID. Sets the minimum
     * number of values to match to 1.
     * 
     * @param id
     *            the proposition ID, a {@link String}
     */
    public CompoundLowLevelAbstractionDefinition(String id) {
        super(id);
        this.lowLevelIds = new HashSet<String>();
        this.classificationMatrix = new LinkedHashMap<String, Map<String, Value>>();
        this.minimumNumberOfValues = 1;
    }

    /**
     * Gets the IDs of the low-level abstraction that this abstraction is
     * abstracted from. Identical to {@link #getAbstractedFrom()}.
     * 
     * @return a {@link Set} of {@link String}s that are the proposition IDs of
     *         the underlying low-level abstractions
     */
    public Set<String> getLowLevelAbstractionIds() {
        return Collections.unmodifiableSet(lowLevelIds);
    }

    /**
     * Gets the minimum number of consecutive intervals that must have the same
     * value before this abstraction is asserted.
     * 
     * @return the minimum number of values, an <code>int</code>
     */
    public int getMinimumNumberOfValues() {
        return minimumNumberOfValues;
    }

    /**
     * Sets the minimum number of consecutive intervals that must have the same
     * value before this abstraction is asserted.
     * 
     * @param minimumNumberOfValues
     *            the minimum number of values, an <code>int</code>
     */
    public void setMinimumNumberOfValues(int minimumNumberOfValues) {
        this.minimumNumberOfValues = minimumNumberOfValues;
    }

    /**
     * Gets the operator that is applied to each interval's low-level
     * abstraction values to determine the value to set for this abstraction.
     * 
     * @return a {@link ValueDefinitionMatchOperator}
     */
    public ValueDefinitionMatchOperator getValueDefinitionMatchOperator() {
        return valueDefinitionMatchOperator;
    }

    /**
     * Sets the operator that is applied to each interval's low-level
     * abstraction values to determine the value to set for this abstraction.
     * 
     * @param valueDefinitionMatchOperator
     *            the match operator to set, a
     *            {@link ValueDefinitionMatchOperator}
     */
    public void setValueDefinitionMatchOperator(
            ValueDefinitionMatchOperator valueDefinitionMatchOperator) {
        this.valueDefinitionMatchOperator = valueDefinitionMatchOperator;
    }

    /**
     * The given proposition ID is added as an abstracted-from relationship. The
     * ID should match that of a {@link LowLevelAbstractionDefinition}.
     * 
     * @param id
     *            the proposition ID, a {@link String}
     */
    public void addLowLevelAbstractionId(String id) {
        this.lowLevelIds.add(id);
    }

    /**
     * Adds a value classification for the abstraction. If a classification of
     * the provided name doesn't exist, then it is created. The value definition
     * name must be one of the values applicable to the low-level abstraction
     * whose ID is also passed in.
     * 
     * @param id
     *            the name of the value definition
     * @param lowLevelAbstractionId
     *            the name of the low-level abstraction to add to the
     *            classification
     * @param lowLevelValueDefName
     *            the name of the low-level abstraction's value
     */
    public void addValueClassification(String id, String lowLevelAbstractionId,
            String lowLevelValueDefName) {
        if (!classificationMatrix.containsKey(id)) {
            classificationMatrix.put(id, new HashMap<String, Value>());
        }
        if (!lowLevelIds.contains(lowLevelAbstractionId)) {
            lowLevelIds.add(lowLevelAbstractionId);
        }
        classificationMatrix.get(id).put(lowLevelAbstractionId,
                NominalValue.getInstance(lowLevelValueDefName));
    }

    LinkedHashMap<String, Map<String, Value>> getValueClassifications() {
        return classificationMatrix;
    }

    @Override
    public Set<String> getAbstractedFrom() {
        return getLowLevelAbstractionIds();
    }

    /**
     * Sets whether this type of compound low-level abstraction is concatenable.
     * 
     * @param concatenable
     *            <code>true</code> if concatenable, <code>false</code> if not.
     */
    public void setConcatenable(boolean concatenable) {
        this.concatenable = concatenable;
    }

    @Override
    public boolean isConcatenable() {
        return this.concatenable;
    }

    @Override
    public boolean isSolid() {
        return true;
    }

    @Override
    public void acceptChecked(
            PropositionDefinitionCheckedVisitor propositionVisitor)
            throws ProtempaException {
        if (propositionVisitor == null) {
            throw new IllegalArgumentException(
                    "propositionVisitor cannot be null");
        }
        propositionVisitor.visit(this);
    }

    @Override
    public void accept(PropositionDefinitionVisitor propositionVisitor) {
        if (propositionVisitor == null) {
            throw new IllegalArgumentException(
                    "propositionVisitor cannot be null");
        }
        propositionVisitor.visit(this);
    }

    @Override
    protected void recalculateChildren() {
        String[] old = this.children;
        this.children = this.lowLevelIds.toArray(new String[this.lowLevelIds
                .size()]);
        if (this.changes != null) {
            this.changes.firePropertyChange(CHILDREN_PROPERTY, old,
                    this.children);
        }
    }

}