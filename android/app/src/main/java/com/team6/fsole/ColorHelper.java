package com.team6.fsole;

import android.util.Log;

/**
 * Created by Owner on 4/22/2017.
 */

public class ColorHelper
{
    public static final int B_OUTTER = 0;
    public static final int T_INNER = 1;
    public static final int T_OUTTER = 2;
    public static final int B_INNER = 3;

    private static int BLUE = R.drawable.blue;
    private static int GREEN = R.drawable.green;
    private static int YELLOW = R.drawable.yellow;
    private static int ORANGE = R.drawable.orange;
    private static int RED = R.drawable.red;

    ColorHelper()
    {

    }

    int getColorFromRawSensorValue(int input, int position)
    {
        // Get sigmoid of our value
        double normalized_input = 1.00 / (1.00 + Math.exp(-(double)input/1024.00));
        Log.i("ColorHelper", "Got normalized value of " + normalized_input);

        // Based on several values gathered from prior analysis of 10 users, return a color
        switch (position)
        {
            case B_OUTTER:
                return bottomOutterRangeCheck(normalized_input);
            case T_INNER:
                return topInnerRangeCheck(normalized_input);
            case T_OUTTER:
                return topOutterRangeCheck(normalized_input);
            default:
                return bottomInnerRangeCheck(normalized_input);
        }
    }

    private int topInnerRangeCheck(double normalized_input)
    {
        if (normalized_input > 0.578) return RED;
        else if (normalized_input <= 0.578 && normalized_input > 0.560) return ORANGE;
        else if (normalized_input <= 0.560 && normalized_input > 0.543) return YELLOW;
        else if (normalized_input <= 0.543 && normalized_input > 0.526) return GREEN;
        else return BLUE;
    }

    private int topOutterRangeCheck(double normalized_input)
    {
        if (normalized_input > 0.686) return RED;
        else if (normalized_input <= 0.686 && normalized_input > 0.662) return ORANGE;
        else if (normalized_input <= 0.662 && normalized_input > 0.637) return YELLOW;
        else if (normalized_input <= 0.637 && normalized_input > 0.613) return GREEN;
        else return BLUE;
    }

    private int bottomInnerRangeCheck(double normalized_input)
    {
        if (normalized_input > 0.695) return RED;
        else if (normalized_input <= 0.695 && normalized_input > 0.667) return ORANGE;
        else if (normalized_input <= 0.667 && normalized_input > 0.640) return YELLOW;
        else if (normalized_input <= 0.640 && normalized_input > 0.613) return GREEN;
        else return BLUE;
    }

    private int bottomOutterRangeCheck(double normalized_input)
    {
        if (normalized_input > 0.684) return RED;
        else if (normalized_input <= 0.684 && normalized_input > 0.652) return ORANGE;
        else if (normalized_input <= 0.652 && normalized_input > 0.619) return YELLOW;
        else if (normalized_input <= 0.619 && normalized_input > 0.587) return GREEN;
        else return BLUE;
    }
}
