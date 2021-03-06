/*
 * #%L
 * Protempa Commons INI Backend Configurations
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
package org.protempa.bconfigs.ini4j;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.arp.javautil.io.TempDirectoryCreator;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Andrew Post
 */
public class INIConfigurationsPathnameTest extends AbstractINIConfigurationsTest {

    @Test
    public void testDefault() {
        assertEquals(INIConfigurations.DEFAULT_DIRECTORY,
                new INIConfigurations().getDirectory());
    }

    @Test
    public void testSystemProperty() {
        String tempDirPath = FileUtils.getTempDirectoryPath();
        String dirSysProp = INIConfigurations.DIRECTORY_SYSTEM_PROPERTY;
        System.setProperty(dirSysProp, tempDirPath);
        INIConfigurations configs = new INIConfigurations();
        System.clearProperty(dirSysProp);
        assertEquals(new File(tempDirPath), configs.getDirectory());
        
    }
    
    @Test
    public void testManuallySpecified() throws IOException {
        TempDirectoryCreator tmpDirCreator = new TempDirectoryCreator();
        File dir = tmpDirCreator.create("foo", "bar", null);
        INIConfigurations configs = new INIConfigurations(dir);
        assertEquals(dir, configs.getDirectory());
    }
    
}
