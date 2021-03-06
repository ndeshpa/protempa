/*
 * #%L
 * Protempa Commons Backend Provider
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
package org.protempa.backend.dsb.relationaldb;

public abstract class AbstractJoinClause implements JoinClause {

    private final JoinSpec.JoinType joinType;
    
    protected AbstractJoinClause(JoinSpec.JoinType joinType) {
        this.joinType = joinType;
    }
    
    @Override
    public String generateClause() {
        switch (joinType) {
            case INNER:
                return " JOIN ";
            case LEFT_OUTER:
                return" LEFT OUTER JOIN ";
            default:
                throw new AssertionError("invalid join type: " + joinType);
        }
    }

}
