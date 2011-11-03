package org.protempa;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.logging.Level;

import org.drools.WorkingMemory;
import org.drools.spi.Consequence;
import org.drools.spi.KnowledgeHelper;
import org.protempa.proposition.Sequence;
import org.protempa.proposition.TemporalProposition;

/**
 * @author Andrew Post
 */
final class LowLevelAbstractionConsequence implements Consequence {

    private static final long serialVersionUID = 2455607587534331595L;
    private final LowLevelAbstractionDefinition def;
    private final Algorithm algorithm;
    private final DerivationsBuilder derivationsBuilder;
    private transient MyObjectAsserter objAsserter;

    private static class MyObjectAsserter implements ObjectAsserter {

        private WorkingMemory workingMemory;

        @Override
        public void assertObject(Object obj) {
            workingMemory.insert(obj);
            ProtempaUtil.logger().log(Level.FINER, "Asserted derived proposition {0}", obj);
        }
    }

    LowLevelAbstractionConsequence(
            LowLevelAbstractionDefinition simpleAbstractionDef,
            Algorithm algorithm, DerivationsBuilder derivationsBuilder) {
        this.def = simpleAbstractionDef;
        this.algorithm = algorithm;
        this.derivationsBuilder = derivationsBuilder;
        this.objAsserter = new MyObjectAsserter();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void evaluate(KnowledgeHelper arg0, WorkingMemory arg1)
            throws Exception {
        List<TemporalProposition> pl =
                (List<TemporalProposition>) arg0.get(
                arg0.getDeclaration("result"));

        @SuppressWarnings("rawtypes")
        Sequence seq = new Sequence(this.def.getAbstractedFrom(), pl);

        objAsserter.workingMemory = arg1;
        LowLevelAbstractionFinder.process(seq, this.def, this.algorithm,
                objAsserter, this.derivationsBuilder);
        objAsserter.workingMemory = null;
    }
    
    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.objAsserter = new MyObjectAsserter();
    }
}
