package org.protempa.bp.commons.dsb.sqlgen;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.arp.javautil.collections.Collections;
import org.arp.javautil.map.CacheMap;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.UniqueIdentifier;

public class ResultCache<P extends Proposition> implements Serializable {
    private static final long serialVersionUID = 7167245069602064844L;
    transient private final Map<String, List<P>> patientCache;
    transient private final Map<UniqueIdentifier, List<UniqueIdentifier>> referencesCache;
    transient private final Map<UniqueIdentifier, Location> conversionMap;

    public ResultCache() {
        this.patientCache = new CacheMap<String, List<P>>();
        this.referencesCache = new CacheMap<UniqueIdentifier, List<UniqueIdentifier>>();
        this.conversionMap = new CacheMap<UniqueIdentifier, Location>();
    }

    public List<P> getPatientPropositions(String patientKey) {
        return this.patientCache.get(patientKey);
    }

    public P getProposition(UniqueIdentifier uid) {
        Location loc = this.conversionMap.get(uid);
        assert loc != null : "Could not find the location for proposition "
                + uid;
        return this.patientCache.get(loc.getPatientKey()).get(loc.getIndex());
    }

    public List<UniqueIdentifier> getReferences(UniqueIdentifier uid) {
        return this.referencesCache.get(uid);
    }

    public Set<UniqueIdentifier> getReferenceKeys() {
        return this.referencesCache.keySet();
    }

    public void addReference(UniqueIdentifier uid, UniqueIdentifier reference) {
        // there should ever be a case where the proposition is
        // added to the results BEFORE it's added as part of a patient's
        // proposition list
        assert this.conversionMap.containsKey(uid) : "Proposition being put is not contained in any patient's proposition list";

        // now, we add to or update the propositions cache
        Collections.putList(this.referencesCache, uid, reference);
    }

    public void put(String key, List<P> propList) {
        // first, we add to or update the patients cache
        this.patientCache.put(key, propList);

        // now we add to or update the propositions cache, and also record
        // the mapping from one cache to the other in the conversion map
        int index;
        for (index = 0; index < propList.size(); index++) {
            P prop = propList.get(index);
            UniqueIdentifier uid = prop.getUniqueIdentifier();

            // we add a conversion from the UID to a location in the
            // patients cache if it doesn't already exist
            if (!this.conversionMap.containsKey(uid)) {
                Location loc = new Location(key, index);
                this.conversionMap.put(uid, loc);
            }
        }
    }

    public Map<String, List<P>> getPatientCache() {
        return this.patientCache;
    }

    class Location implements Serializable {
        private static final long serialVersionUID = 5829710457303104633L;
        private String patientKey;
        private int index;

        public Location(String key, int idx) {
            this.patientKey = key;
            this.index = idx;
        }

        public String getPatientKey() {
            return patientKey;
        }

        public void setPatientKey(String patientKey) {
            this.patientKey = patientKey;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }
    }
}