package org.protempa.proposition;

import java.beans.PropertyChangeListener;
import java.text.Format;
import org.apache.commons.lang.builder.ToStringBuilder;

import org.protempa.ProtempaException;
import org.protempa.proposition.value.Granularity;

/**
 * An external volitional action or process, such as the administration of a
 * drug (as opposed to a measurable datum, such as temperature). Events cannot
 * be abstracted from other data.
 * 
 * @author Andrew Post
 */
public final class Event extends TemporalProposition {

    private static final long serialVersionUID = -47155268578773061L;

    /**
     * Creates an event with an id and no attribute id.
     *
     * @param id
     *            an identification <code>String</code> for this event.
     */
    public Event(String id) {
        super(id);
    }

    @Override
    public String getStartRepr() {
        Granularity startGran = getStartGranularity();
        return formatStart(startGran != null ? startGran.getReprFormat() :
            null);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.virginia.pbhs.parameters.Parameter#getStartFormattedLong()
     */
    @Override
    public String getStartFormattedLong() {
        Granularity startGran = getStartGranularity();
        return formatStart(startGran != null ? startGran.getLongFormat() : null);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.protempa.proposition.TemporalProposition#getFinishRepr()
     */
    @Override
    public String getFinishRepr() {
        Granularity finishGran = getFinishGranularity();
        return formatFinish(finishGran != null ? finishGran.getReprFormat()
                : null);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.virginia.pbhs.parameters.Parameter#getFinishFormattedLong()
     */
    @Override
    public String getFinishFormattedLong() {
        Granularity finishGran = getFinishGranularity();
        return formatFinish(finishGran != null ? finishGran.getLongFormat()
                : null);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.virginia.pbhs.parameters.Parameter#getStartFormattedMedium()
     */
    @Override
    public String getStartFormattedMedium() {
        Granularity startGran = getStartGranularity();
        return formatStart(startGran != null ? startGran.getMediumFormat()
                : null);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.virginia.pbhs.parameters.Parameter#getFinishFormattedMedium()
     */
    @Override
    public String getFinishFormattedMedium() {
        Granularity finishGran = getFinishGranularity();
        return formatFinish(finishGran != null ? finishGran.getMediumFormat()
                : null);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.virginia.pbhs.parameters.Parameter#getStartFormattedShort()
     */
    @Override
    public String getStartFormattedShort() {
        Granularity startGran = getStartGranularity();
        return formatStart(startGran != null ? startGran.getShortFormat()
                : null);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.virginia.pbhs.parameters.Parameter#getFinishFormattedShort()
     */
    @Override
    public String getFinishFormattedShort() {
        Granularity finishGran = getFinishGranularity();
        return formatFinish(finishGran != null ? finishGran.getShortFormat()
                : null);
    }

    /**
     * Uses the given <code>Format</code> to format the start of this
     * parameter's interval.
     *
     * @param format
     *            a <code>Format</code> object.
     * @return the start of this parameter's interval as a formatted
     *         <code>String</code>.
     */
    private String formatStart(Format format) {
        Interval interval = getInterval();
        if (format != null && interval != null) {
            return format.format(interval.getMinimumStart());
        } else if (interval != null) {
            return "" + interval.getMinimumStart();
        } else {
            return null;
        }
    }

    /**
     * Uses the given <code>Format</code> to format the finish of this
     * parameter's interval.
     *
     * @param format
     *            a <code>Format</code> object.
     * @return the finish of this parameter's interval as a formatted
     *         <code>String</code>.
     */
    private String formatFinish(Format format) {
        Interval interval = getInterval();
        if (format != null && interval != null) {
            return format.format(interval.getMinimumFinish());
        } else if (interval != null) {
            return "" + interval.getMinimumFinish();
        } else {
            return null;
        }
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener l) {
        this.changes.addPropertyChangeListener(l);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener l) {
        this.changes.removePropertyChangeListener(l);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.protempa.proposition.TemporalProposition#isEqual(java.lang.Object)
     */
    @Override
    public boolean isEqual(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Event)) {
            return false;
        }

        Event a = (Event) o;
        return super.isEqual(a);
    }

    @Override
    public void accept(PropositionVisitor propositionVisitor) {
        propositionVisitor.visit(this);
    }

    @Override
    public void acceptChecked(
            PropositionCheckedVisitor propositionCheckedVisitor)
            throws ProtempaException {
        propositionCheckedVisitor.visit(this);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .appendSuper(super.toString())
                .toString();
    }
}
