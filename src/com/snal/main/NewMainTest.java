/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.snal.main;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author csandy
 */
public class NewMainTest {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date d1 = sdf.parse("20161001");
        Calendar oldday = Calendar.getInstance();
        oldday.setTime(d1);
        Calendar now = Calendar.getInstance();
        while (now.after(oldday)) {
            now.add(Calendar.DAY_OF_MONTH, -1);
            String data = sdf.format(now.getTime());
            System.out.println(sdf.format(now.getTime()));
        }
        System.out.println(Integer.toHexString(213));
    }

}
