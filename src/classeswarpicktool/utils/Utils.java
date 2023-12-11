/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package classeswarpicktool.utils;

import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author Ducnm62
 */
public class Utils {

    public static String getTimestamp() {
        Calendar calendar = Calendar.getInstance();
        Date time = calendar.getTime();
        String date = (time.getYear() - 100)
                + "" + (time.getMonth() < 10 ? "0" + (time.getMonth() + 1) : (time.getMonth() + 1))
                + "" + (time.getDate() < 10 ? "0" + time.getDate() : time.getDate())
                + "" + "_" + (time.getHours() < 10 ? "0" + time.getHours() : time.getHours())
                + "" + (time.getMinutes() < 10 ? "0" + time.getMinutes() : time.getMinutes());
        return date;
    }
}
