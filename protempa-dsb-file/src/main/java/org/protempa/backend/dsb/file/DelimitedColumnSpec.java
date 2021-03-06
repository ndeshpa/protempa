package org.protempa.backend.dsb.file;

/*
 * #%L
 * Protempa File Data Source Backend
 * %%
 * Copyright (C) 2012 - 2015 Emory University
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

import au.com.bytecode.opencsv.CSVParser;
import java.io.IOException;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author Andrew Post
 */
public class DelimitedColumnSpec extends ColumnSpec {

    private int index;
    private final CSVParser parser;

    public DelimitedColumnSpec() {
        this.parser = new CSVParser('|');
    }
    
    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    void parseDescriptor(String descriptor) throws IOException {
        String[] parseLine = this.parser.parseLine(descriptor);
        this.index = Integer.parseInt(parseLine[0]);
        String rest = StringUtils.join(parseLine, '|', 1, parseLine.length);
        setLinks(rest);
    }

}
