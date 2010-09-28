package org.protempa;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.arp.javautil.collections.Collections;
import org.drools.WorkingMemory;
import org.protempa.proposition.AbstractParameter;
import org.protempa.proposition.AbstractPropositionVisitor;
import org.protempa.proposition.Context;
import org.protempa.proposition.DerivedUniqueIdentifier;
import org.protempa.proposition.Event;
import org.protempa.proposition.PrimitiveParameter;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.UniqueIdentifier;

/**
 * TODO We should delegate the copying to another copying class.
 * 
 * @author Andrew Post
 */
class PropositionCopier extends AbstractPropositionVisitor {

    private final String propId;
    private final WorkingMemory arg1;
    private final Map<Proposition, List<Proposition>> derivations;

    PropositionCopier(String propId, WorkingMemory workingMemory,
            Map<Proposition, List<Proposition>> derivations) {
        super();
        this.derivations = derivations;
        this.propId = propId;
        this.arg1 = workingMemory;
    }

    @Override
    public void visit(AbstractParameter p) {
        AbstractParameter param = new AbstractParameter(propId);
        param.setDataSourceType(new DerivedDataSourceType());
        param.setUniqueIdentifier(new UniqueIdentifier(new DerivedSourceId(),new DerivedUniqueIdentifier(UUID.randomUUID().toString())));
        param.setInterval(p.getInterval());
        param.setValue(p.getValue());
        this.arg1.insert(param);
    }

    @Override
    public void visit(Event event) {
        Event e = new Event(propId);
        e.setInterval(event.getInterval());
        Collections.putList(this.derivations, event, e);
        Collections.putList(this.derivations, e, event);
        this.arg1.insert(e);
    }

    @Override
    public void visit(PrimitiveParameter primitiveParameter) {
        PrimitiveParameter param = new PrimitiveParameter(propId);
        param.setInterval(primitiveParameter.getInterval());
        param.setValue(primitiveParameter.getValue());
        Collections.putList(this.derivations, primitiveParameter, param);
        Collections.putList(this.derivations, param, primitiveParameter);
        this.arg1.insert(param);
    }

    @Override
    public void visit(Context context) {
    }
}
