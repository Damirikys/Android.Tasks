package ru.urfu.taskmanager;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import ru.urfu.taskmanager.tools.NetworkUtil;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class InstrumentedUtilsTest
{
    @Test
    public void networkToolsTest() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();
        assertEquals(true, NetworkUtil.networkIsReachable(appContext));
    }
}
