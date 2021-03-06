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

import org.protempa.backend.Backend;
import org.protempa.backend.BackendUpdatedEvent;

/**
 * The event generated when the algorithm source is updated.
 *
 * @author Andrew Post
 */
public final class AlgorithmSourceUpdatedEvent extends SourceUpdatedEvent {

    private static final long serialVersionUID = 2757359115821263581L;
    
    private final AlgorithmSource algorithmSource;

    /**
     * Constructs an event with the source
     * <code>AlgorithmSource</code> that generated the event.
     *
     * @param algorithmSourceBackend an <code>AlgorithmSourceBackend</code>.
     */
    public AlgorithmSourceUpdatedEvent(AlgorithmSource algorithmSource) {
        super(algorithmSource);
        this.algorithmSource = algorithmSource;
    }

    @Override
    public AlgorithmSource getProtempaSource() {
        return this.algorithmSource;
    }
}
