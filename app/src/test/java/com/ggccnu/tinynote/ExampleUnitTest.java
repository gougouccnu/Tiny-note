package com.ggccnu.tinynote;

import com.ggccnu.tinynote.util.DateConvertor;

import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void dateConvertorTest() throws Exception {
        assertEquals(DateConvertor.formatDay(0), "零日");
        assertEquals(DateConvertor.formatDay(10), "一拾日");
        assertEquals(DateConvertor.formatDay(30), "三拾日");
        assertEquals(DateConvertor.formatDay(31), "三拾一日");
    }

    @Test
    public void emailValidTest() throws Exception {
        assertEquals(isAccountValid("78006456@qq.com"), true);
        assertEquals(isAccountValid("78006456@QQ.com"), true);
        assertEquals(isAccountValid("78006456@qq.cn"), true);

        assertEquals(false, isAccountValid("7#8006456@qq.com"));
        assertEquals(true, isAccountValid("780064w56@qq.sina.com"));
        assertEquals(isAccountValid("78006a56@qq.com"), true);
    }

    private boolean isAccountValid(String email) {

        Pattern p = Pattern.compile("\\w+@(\\w+.)+[a-z]{2,3}");
        Matcher m = p.matcher(email);
        return m.matches();
    }
}