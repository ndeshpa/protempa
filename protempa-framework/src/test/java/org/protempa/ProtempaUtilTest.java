/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.protempa;

import junit.framework.TestCase;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 *
 * @author arpost
 */
public class ProtempaUtilTest extends TestCase {

    public ProtempaUtilTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    @Override
    public void setUp() {
    }

    @After
    @Override
    public void tearDown() {
    }

    public void testCheckArrayForDuplicatesNoDupeLength0() {
        String[] arr = {};
        try {
            ProtempaUtil.checkArrayForDuplicates(arr, "arr");
        } catch (IllegalArgumentException iae) {
            fail();
        }
    }

    public void testCheckArrayForDuplicatesNoDupeLength1() {
        String[] arr = {"test"};
        try {
            ProtempaUtil.checkArrayForDuplicates(arr, "arr");
        } catch (IllegalArgumentException iae) {
            fail();
        }
    }

    public void testCheckArrayForDuplicatesNoDupeLength1Null() {
        String[] arr = {null};
        try {
            ProtempaUtil.checkArrayForDuplicates(arr, "arr");
        } catch (IllegalArgumentException iae) {
            fail();
        }
    }

    public void testCheckArrayForDuplicatesDupeLength2() {
        String[] arr = {"test", "test"};
        try {
            ProtempaUtil.checkArrayForDuplicates(arr, "arr");
            fail();
        } catch (IllegalArgumentException iae) {
        }
    }

    public void testCheckArrayForDuplicatesDupeLength2Null() {
        String[] arr = {null, null};
        try {
            ProtempaUtil.checkArrayForDuplicates(arr, "arr");
            fail();
        } catch (IllegalArgumentException iae) {
        }
    }

    public void testCheckArrayForDuplicatesDupeLength3() {
        String[] arr = {"test", "test", "test3"};
        try {
            ProtempaUtil.checkArrayForDuplicates(arr, "arr");
            fail();
        } catch (IllegalArgumentException iae) {
        }
    }

    public void testCheckArrayForDuplicatesDupeLength3Null() {
        String[] arr = {null, null, "test3"};
        try {
            ProtempaUtil.checkArrayForDuplicates(arr, "arr");
            fail();
        } catch (IllegalArgumentException iae) {
        }
    }

    public void testCheckArrayForDuplicatesNoDupeLength2() {
        String[] arr = {"test", "test2"};
        try {
            ProtempaUtil.checkArrayForDuplicates(arr, "arr");
        } catch (IllegalArgumentException iae) {
            fail();
        }
    }

    public void testCheckArrayForDuplicatesNoDupeLength2Null() {
        String[] arr = {"test", null};
        try {
            ProtempaUtil.checkArrayForDuplicates(arr, "arr");
        } catch (IllegalArgumentException iae) {
            fail();
        }
    }

    public void testCheckArrayForDuplicatesNoDupeLength3() {
        String[] arr = {"test", "test2", "test3"};
        try {
            ProtempaUtil.checkArrayForDuplicates(arr, "arr");
        } catch (IllegalArgumentException iae) {
            fail();
        }
    }

    public void testCheckArrayForDuplicatesNoDupeLength3Null() {
        String[] arr = {"test", null, "test3"};
        try {
            ProtempaUtil.checkArrayForDuplicates(arr, "arr");
        } catch (IllegalArgumentException iae) {
            fail();
        }
    }


}