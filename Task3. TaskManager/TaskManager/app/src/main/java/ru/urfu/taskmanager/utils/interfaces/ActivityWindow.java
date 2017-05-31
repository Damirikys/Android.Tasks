package ru.urfu.taskmanager.utils.interfaces;

import android.content.Context;
import android.content.res.Resources;

interface ActivityWindow
{
    Context getBaseContext();

    Context getApplicationContext();

    Resources getResources();
}
