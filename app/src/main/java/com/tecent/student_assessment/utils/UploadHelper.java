package com.tecent.student_assessment.utils;

import android.os.AsyncTask;
import android.util.Log;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static android.content.ContentValues.TAG;

public class UploadHelper extends AsyncTask<String, Void, String> {

    private DataOutputStream dos = null;
    private FileInputStream fis = null;
    private BufferedReader br = null;
    private String NEW_LINE = "\r\n";
    private String SPACER = "--";
    private String BOUNDARY = "*****";
    private int MAX_BUFFER_SIZE=10*1024*1024;
    private String urlLink;
    private String serverResponse;
    private String fileName="";

    public UploadHelper(String urlLink) {
        this.urlLink=urlLink;
    }
    public UploadHelper(String urlLink, String fileName) {
        this.urlLink=urlLink;
        this.fileName=fileName;
    }

    protected void onPreExecute() {
    }

    @Override
    protected String doInBackground(String... arg0) {

        try {
            File f = new File(arg0[0]);
            URL url = new URL(urlLink);
            int bytesRead;
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
            if(fileName.equals("")){
                fileName=f.getName();
            }
            String contentDisposition = "Content-Disposition: form-data; name=\"myFile\"; filename=\""
                    + fileName + "\"";
            String contentType = "Content-Type: application/octet-stream";


            dos = new DataOutputStream(conn.getOutputStream());
            fis = new FileInputStream(f);


            dos.writeBytes(SPACER + BOUNDARY + NEW_LINE);
            dos.writeBytes(contentDisposition + NEW_LINE);
            dos.writeBytes(contentType + NEW_LINE);
            dos.writeBytes(NEW_LINE);
            byte[] buffer = new byte[MAX_BUFFER_SIZE];
            while ((bytesRead = fis.read(buffer)) != -1) {
                dos.write(buffer, 0, bytesRead);
            }
            dos.writeBytes(NEW_LINE);
            dos.writeBytes(SPACER + BOUNDARY + SPACER);
            dos.flush();

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                Log.w(TAG,
                        responseCode + " Error: " + conn.getResponseMessage());
                return null;
            }

            br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            Log.d(TAG, "Successfully uploaded " + f.getName());
            serverResponse=sb.toString();

        } catch (Exception e) {
            Log.d("uperror",e.toString());
        } finally {
            try {
                dos.close();
                if (fis != null)
                    fis.close();
                if (br != null)
                    br.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return String.valueOf(serverResponse);
    }


    @Override
    protected void onPostExecute(String result) {
    }

    public String getServerResponse() {
        return serverResponse;
    }
}