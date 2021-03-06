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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.ParseException;

import junit.framework.TestCase;

/**
 * Test cases for RelativeHourUnit.
 * 
 * @author Andrew Post
 */
public class RelativeHourUnitTest extends TestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testGetDurationInHours() {
		assertEquals(2, Math.round(RelativeHourUnit.HOUR
				.length(2 * 60 * 1000 * 60)));
	}

	public void testSerializable() throws IOException, ClassNotFoundException {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(bytes);
		out.writeObject(RelativeHourUnit.HOUR);
		out.close();

		ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(
				bytes.toByteArray()));
		Unit unit = (Unit) in.readObject();
		assertEquals(RelativeHourUnit.HOUR, unit);
		in.close();
	}

	public void testReallyLongDuration() {
		assertEquals(596, RelativeHourUnit.HOUR.length(Integer.MAX_VALUE));
	}

	public void testDistanceBetween20Hours() throws ParseException {
		assertEquals(20, RelativeHourGranularity.HOUR.distance(0L,
				20L * 60 * 60 * 1000, RelativeHourGranularity.HOUR,
				RelativeHourUnit.HOUR));
	}
}
