package com.sayyi.software.tbp.generator.dsl.fieldstr;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author xuchuang
 * @date 2021/5/18
 */
public class JTypeFactoryTest {

    @Test
    public void test_get() {
        JType jType = JTypeFactory.get("string");
        Assert.assertEquals(JString.class, jType.getClass());
    }

}
