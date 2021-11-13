package com.example;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.HttpsURLConnection;

/**
 * 帳票データ CSV 一括取込 APIサンプル
 * 
 * 【免責及び同意事項】 本プログラムはサンプルとして無償で提供しているものになります。 お客様は以下の事項全てに同意の上、本プログラムを使用するものとし、
 * 株式会社ラクス（以下「当社」といいます）は、お客様が本プログラムを使用した時点において 以下の事項全てに同意したものとみなします。
 * 
 * ・当社は、本プログラムに関する動作を保証するものではありません。
 * 
 * ・当社は本プログラムに不具合等があった場合においても、修正その他いかなる保守作業をする義務を負いません。
 * 
 * ・本プログラムの使用により、お客様または第三者に損害が生じた場合でも、 当社は一切の責任を負いません。自己責任においてご利用ください。
 * 
 * ・本プログラムにかかる著作権（著作権法第27条及び第28条に定める権利を含む） その他の知的財産権を含むすべての権利は、当社に帰属します。
 * 
 * ・当社は「楽楽明細」をご検討、ご契約中のお客様にのみ本プログラムを提供しています。
 * お客様は本プログラムを自己以外の第三者に提供してはならないものとします。
 * また、お客様は本プログラムを「楽楽明細」にかかるAPI連携処理の検証目的以外で使用してはならないものとします。
 * 
 * ・ラクスでは、本プログラムの記載内容に関する保守やサポートは承っておりません。
 * API連携実施時のエラー解析のみ承りますので、ご希望の際には以下の情報をお送りください。 ①API連携時の送信電文（ヘッダー部を含む） ②電文の送信日時
 * ③上記①の送信電文に対するレスポンス電文（ヘッダー部を含む）
 * 
 * ・Java以外の言語のサンプルプログラムの提供はいたしかねます。
 * 
 * ・お客様が上記事項のいずれかに違反した結果、当社が損害を被った場合、 お客様は当社の被った損害を賠償する責任を負うものとします。
 */

public class ApiSample02 {
    // 接続先 設定
    private static final String API_URL = "https://ta.eco-serv.jp/gbrc/api/v1/customers/imports"; // お客様環境の【ドメイン】、【アカウント】を指定
    private static final String API_TOKEN = "AZsH5M76XPXASjHAkEcIGOKtfjUL46CXrpadiQJfh9srDghuGLp6hxXC0DTfegbK"; // API設定の【APIトークン】を指定

    // boundary文字列 設定
    private static final String BOUNDARY = "======" + System.currentTimeMillis();
    private static final String CRLF = "\r\n";
    private static final String DOUBLE_HYPHEN = "--";

    // 取込ファイル 設定
    private static final String CSV_PATH = "C:\\Users\\toshikanyama\\Google ドライブ\\Work\\情報システムWG\\経理課関連\\顧客データ\\顧客20211112.csv"; // 取込対象のファイルパスを指定

    public static void main(String[] args) throws Exception {
        URL url = new URL(API_URL);
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setUseCaches(false);
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
        conn.setRequestProperty("X-WB-apitoken", API_TOKEN);

        try (OutputStream os = conn.getOutputStream();
                PrintWriter writer = new PrintWriter(
                        new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8)));
                PrintWriter csvWriter = new PrintWriter(
                        new BufferedWriter(new OutputStreamWriter(os, "Windows-31J")))) {

            // JSONパラメータ 設定(サンプル)
            writer.append(DOUBLE_HYPHEN).append(BOUNDARY).append(CRLF);
            writer.append("Content-Disposition: form-data; name=\"json\"").append(CRLF);
            writer.append("Content-Type: application/json; charset=UTF-8").append(CRLF);
            writer.append(CRLF);
            writer.append("{" + "\"ispdatenfo\":0," + "\"updateBlank\":2," + "\"importProcessName\":\"顧客データ取込サンプル\","
                    + "\"skipFirst\":1," + "\"settingId\":5" + "}");
            writer.append(CRLF);

            // CSVパラメータ 設定
            writer.append(DOUBLE_HYPHEN).append(BOUNDARY).append(CRLF);
            writer.append("Content-Disposition: form-data; name=\"files[0]\"; fileName=\"顧客20211112.csv\"")
                    .append(CRLF);
            writer.append("Content-Type: text/csv; charset=Windows-31J").append(CRLF);
            writer.append(CRLF);
            writer.flush();

            // CSVファイル読込
            try (BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(CSV_PATH), "Windows-31J"))) {
                String text;
                while ((text = bufferedReader.readLine()) != null) {
                    csvWriter.append(text).append(CRLF);
                }
                csvWriter.flush();
            }

            writer.append(CRLF);

            writer.append(CRLF);
            writer.append(DOUBLE_HYPHEN).append(BOUNDARY).append(DOUBLE_HYPHEN).append(CRLF);
            writer.flush();
        }

        // レスポンス確認
        int status = conn.getResponseCode();
        if (status == HttpURLConnection.HTTP_OK) {
            System.out.println("正常終了しました");
            String line;
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }
        } else {
            System.err.println("異常終了しました");
            String line;
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8))) {
                while ((line = reader.readLine()) != null) {
                    System.err.println(line);
                }
            }
        }
        conn.disconnect();
    }
}
