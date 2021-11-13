package com.example;

import java.sql.*;
import java.sql.DriverManager;

class FMPJDBCTest {
    public static void main(String[] args) {
        // JDBC クライアントドライバを登録
        try {
            // Driver d = (Driver)
            // Class.forName("com.filemaker.jdbc.Driver").getDeclaredConstructor().newInstance();
            Class.forName("com.filemaker.jdbc.Driver").getDeclaredConstructor().newInstance();

        } catch (Exception e) {
            System.out.println(e);
        }
        // FileMaker への接続を確立
        Connection con = null;
        ;
        try {
            con = DriverManager.getConnection("jdbc:filemaker://192.168.0.175/受託情報", "kanyama", "soumu2049");
        } catch (Exception e) {
            System.out.println(e);
        }
        // 接続の警告を取得
        SQLWarning warning = null;
        try {
            warning = con.getWarnings();
            if (warning == null) {
                System.out.println(" 警告なし ");
                return;
            }
            while (warning != null) {
                System.out.println(" 警告 : " + warning);
                warning = warning.getNextWarning();
            }

            String selectStatement = "select * from \"受託情報\" where \"受託番号\" = '09B170007'";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(selectStatement);

            while (rs.next()) {
                String title = rs.getString("会社名");
                System.out.println(title);
            }

            con.close();

        } catch (Exception e) {
            System.out.println(e);
        }

    }
}