/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package classeswarpicktool;

import javax.swing.JLabel;

/**
 *
 * @author Ducnm62
 */
public class LogUtils {

    private static javax.swing.JLabel txtStatus;

    public LogUtils() {

    }

    public static void setStatusComponent(JLabel txtStatus) {
        LogUtils.txtStatus = txtStatus;
    }

    public static void setStatus(String status) {
        System.out.println("Update: " + status);
        if (txtStatus == null) {
            return;
        }
        txtStatus.setText("Status: " + status);
    }
}
