package com.yihu.ehr.util;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.03.22 10:16
 */
public class IdValidatorTest {

    @Test
    public void testValidateOrgCode() throws Exception {
        Assert.assertFalse(IdValidator.validateOrgCode("42017976_4"));
        Assert.assertTrue(IdValidator.validateOrgCode("42017976-4"));
    }

    @Test
    public void testValidateIdCardNo() throws Exception {

    }

    @Test
    public void testIsDate() throws Exception {

    }
}