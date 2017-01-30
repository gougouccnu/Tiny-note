package com.ggccnu.tinynote;

import com.ggccnu.tinynote.util.DateConvertor;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 3);
    }

    @Test
    public void dateConvertorTest() throws Exception {
        assertEquals(DateConvertor.formatDay(0), "零日");
        assertEquals(DateConvertor.formatDay(10), "一拾日");
        assertEquals(DateConvertor.formatDay(30), "三拾日");
        assertEquals(DateConvertor.formatDay(31), "三拾日");
    }
}