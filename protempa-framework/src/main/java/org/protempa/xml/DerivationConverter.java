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
package org.protempa.xml;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.mvel.ConversionException;
import org.protempa.KnowledgeSource;
import org.protempa.KnowledgeSourceReadException;
import org.protempa.dest.table.Derivation;
import org.protempa.dest.table.Derivation.Behavior;
import org.protempa.dest.table.PropertyConstraint;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.interval.Relation;
import org.protempa.proposition.value.Value;

/**
 * @author mgrand
 */
class DerivationConverter extends AbstractConverter {

    private static final String RELATION = "relation";
    private static final String ALLOWED_VALUES = "allowedValues";
    private static final String PROPERTY_CONSTRAINTS = "propertyConstraints";
    private static final String PROPOSITION_IDS = "propositionIDs";
    private static final String PROPOSITION_COMPARATOR = "propositionComparator";
    private static final String TO_INDEX = "toIndex";
    private static final String FROM_INDEX = "fromIndex";
    private static final String BEHAVIOR = "behavior";
    
    DerivationConverter(KnowledgeSource knowledgeSource) {
        super(knowledgeSource);
    }

    /* (non-Javadoc)
     * @see com.thoughtworks.xstream.converters.ConverterMatcher#canConvert(java.lang.Class)
     */
    @Override
    public boolean canConvert(@SuppressWarnings("rawtypes") Class type) {
        return Derivation.class.equals(type);
    }

    /* (non-Javadoc)
     * @see com.thoughtworks.xstream.converters.Converter#marshal(java.lang.Object, com.thoughtworks.xstream.io.HierarchicalStreamWriter, com.thoughtworks.xstream.converters.MarshallingContext)
     */
    @Override
    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        Derivation derivation = (Derivation) source;

        BehaviorValueConverter behaviorConverter = new BehaviorValueConverter();
        String behaviorString = behaviorConverter.toString(derivation.getBehavior());
        writer.addAttribute(BEHAVIOR, behaviorString);

        writer.addAttribute(FROM_INDEX, Integer.toString(derivation.getFromIndex()));
        writer.addAttribute(TO_INDEX, Integer.toString(derivation.getToIndex()));

        Comparator<Proposition> comparator = derivation.getComparator();
        if (comparator != null) {
            PropositionComparatorValueConverter converter = new PropositionComparatorValueConverter();
            writer.addAttribute(PROPOSITION_COMPARATOR, converter.toString(comparator));
        }

        writer.startNode(PROPOSITION_IDS);
        context.convertAnother(derivation.getPropositionIds(), new PropIDsConverter(getKnowledgeSource()));
        writer.endNode();

        PropertyConstraint[] constraints = derivation.getConstraints();
        if (constraints != null && constraints.length > 0) {
            writer.startNode(PROPERTY_CONSTRAINTS);
            PropertyConstraintsConverter converter = new PropertyConstraintsConverter(getKnowledgeSource());
            context.convertAnother(constraints, converter);
            writer.endNode();
        }

        Value[] allowedValues = derivation.getAllowedValues();
        if (allowedValues != null && allowedValues.length > 0) {
            writer.startNode(ALLOWED_VALUES);
            AllowedValuesConverter converter = new AllowedValuesConverter(getKnowledgeSource());
            context.convertAnother(allowedValues, converter);
            writer.endNode();
        }
        Relation relation = derivation.getRelation();
        if (relation != null) {
            writer.startNode(RELATION);
            RelationConverter relationConverter = new RelationConverter(getKnowledgeSource());
            context.convertAnother(relation, relationConverter);
            writer.endNode();
        }
    }

    /* (non-Javadoc)
     * @see com.thoughtworks.xstream.converters.Converter#unmarshal(com.thoughtworks.xstream.io.HierarchicalStreamReader, com.thoughtworks.xstream.converters.UnmarshallingContext)
     */
    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        String behaviorString = requiredAttributeValue(reader, BEHAVIOR);
        BehaviorValueConverter behaviorConverter = new BehaviorValueConverter();
        Behavior behavior = (Behavior) behaviorConverter.fromString(behaviorString);

        int fromIndex = intAttributeValue(reader, FROM_INDEX, -1);
        int toIndex = intAttributeValue(reader, TO_INDEX, -1);

        Comparator<Proposition> comparator;
        String comparatorString = reader.getAttribute(PROPOSITION_COMPARATOR);
        if (comparatorString != null) {
            comparator = convertComparatorString(comparatorString);
        } else {
            comparator = null;
        }

        expectChildren(reader);
        reader.moveDown();
        expect(reader, PROPOSITION_IDS);
        String[] propositionIds = (String[]) context.convertAnother(null, String[].class, new PropIDsConverter(getKnowledgeSource()));
        reader.moveUp();

        PropertyConstraint[] constraints = null;
        Value[] allowedValues = null;
        Relation relation = null;

        while (reader.hasMoreChildren()) {
            reader.moveDown();
            if (PROPERTY_CONSTRAINTS.equals(reader.getNodeName())) {
                if (constraints != null) {
                    throw new ConversionException("diversion contains multiple propertyConstraints elements");
                }
                PropertyConstraintsConverter converter = new PropertyConstraintsConverter(getKnowledgeSource());
                constraints = (PropertyConstraint[]) context.convertAnother(null, PropertyConstraint[].class, converter);
                reader.moveUp();
            } else if (ALLOWED_VALUES.equals(reader.getNodeName())) {
                if (allowedValues != null) {
                    throw new ConversionException("diversion contains multiple allowedValues elements");
                }
                AllowedValuesConverter converter = new AllowedValuesConverter(getKnowledgeSource());
                allowedValues = (Value[]) context.convertAnother(null, Value[].class, converter);
                reader.moveUp();
            } else if (RELATION.equals(reader.getNodeName())) {
                if (relation != null) {
                    throw new ConversionException("diversion contains multiple relation elements");
                }
                RelationConverter relationConverter = new RelationConverter(getKnowledgeSource());
                relation = (Relation) context.convertAnother(null, Relation.class, relationConverter);
                reader.moveUp();
            } else {
                throw new ConversionException("Encountered unexpected element: " + reader.getNodeName());
            }
        }
        try {
            return new Derivation(propositionIds, constraints, comparator, fromIndex, toIndex, allowedValues, behavior, relation, getKnowledgeSource());
        } catch (KnowledgeSourceReadException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @SuppressWarnings("unchecked")
    private Comparator<Proposition> convertComparatorString(String comparatorString) {
        PropositionComparatorValueConverter converter = new PropositionComparatorValueConverter();
        return (Comparator<Proposition>) converter.fromString(comparatorString);
    }

}
