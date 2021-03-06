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
package org.protempa.proposition.value;

/**
 * Access to the length units of data provided by a data source. Must have a
 * zero argument constructor.
 *
 * @author Andrew Post
 * @see org.protempa.DataSource#getUnitFactory()
 */
public interface UnitFactory {

    /**
     * Translates the name of a length unit into a {@link Unit} object
     *
     * @param name the name {@link String} of a length unit.
     * @return the {@link Unit} corresponding to the provided name, or
     * <code>null</code> if <code>name</code> is <code>null</code> or no
     * {@link Unit} could be found.
     */
    Unit toUnit(String name);
}
