package com.wmstein.tourcount.database;

/**
 * Created by milo on 05/05/2014.
 * Changed by wmstein on 18.02.2016
 */

public class Count
{
    public int id;
    public int count;
    public String name;
    public String code;
    public String notes;

    public int increase()
    {
        count = count + 1;
        return count;
    }

    public int safe_decrease()
    {
        if (count > 0)
        {
            count = count - 1;
        }
        return count;
    }
    
}