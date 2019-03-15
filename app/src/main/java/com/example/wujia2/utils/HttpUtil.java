package com.example.wujia2.utils;

import okhttp3.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.concurrent.TimeUnit;

public class HttpUtil {

  private static OkHttpClient okHttpClient =
      new OkHttpClient()
          .newBuilder()
          .connectTimeout(10, TimeUnit.SECONDS) // 设置超时时间10s
          .readTimeout(10, TimeUnit.SECONDS) // 设置读取超时时间10s
          .writeTimeout(10, TimeUnit.SECONDS) // 设置写入超时时间10s
          .build();

  /**
   * get同步请求
   *
   * @param requestUrl
   * @return
   * @throws IOException
   */
  public static Response requestGetBySyn(String requestUrl, String token) throws IOException {

    try {
      // 创建一个请求
      Request request =
          new Request.Builder().url(requestUrl).header("Authorization", token).build();

      // 创建一个Call
      Call call = okHttpClient.newCall(request);
      // 执行请求
      Response response = call.execute();

      return response;

    } catch (IOException e) {
      throw new IOException("Inner error " + e);
    }
  }

  /**
   * post同步请求
   *
   * @param requestUrl
   * @param requestParam 请求参数：json字符串
   * @return
   * @throws IOException
   */
  public static Response requestPostBySyn(String requestUrl, String requestParam, String token)
      throws IOException {
    // 创建一个请求
    Request request = null;

    try {
      // 不带参数
      if (requestParam == null) {
        request = new Request.Builder().url(requestUrl).header("Authorization", token).build();
      }
      // 带参数
      else {
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(mediaType, requestParam);
        request =
            new Request.Builder()
                .url(requestUrl)
                .header("Authorization", token)
                .post(requestBody)
                .build();
      }
      // 创建一个Call
      Call call = okHttpClient.newCall(request);
      // 执行请求
      Response response = call.execute();

      return response;

    } catch (IOException e) {
      throw new IOException("Inner error " + e);
    }
  }

  /**
   * post同步提交Form-Data数据请求（上传文件）
   *
   * @param requestUrl
   * @param pathName
   * @return
   * @throws IOException
   */
  public static Response requestPostBySynWithFormData(
      String requestUrl,  String pathName, String fileName) throws IOException {

    RequestBody requestBody =
        new MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "file",
                fileName,
                RequestBody.create(MediaType.parse("application/octet-stream"),  new File(pathName)))
            .build();

    Request request =
        new Request.Builder()
            .url(requestUrl)
            .post(requestBody)
            .build();

    Call call = okHttpClient.newCall(request);
    try {

      Response response = call.execute();
      return response;

    } catch (IOException e) {
      throw new IOException("Inner error " + e);
    }
  }


  /**
   * post同步下载
   *
   * @param requestUrl
   * @param modleId
   * @param downlaodToken
   * @return
   * @throws IOException
   */
  public static boolean requestGetBySynDownload(
      String requestUrl, long modleId, String downloadPath, String downlaodToken, String token)
      throws IOException {

    requestUrl = requestUrl + "/" + modleId + "?token=" + downlaodToken;

    try {

      Request request =
          new Request.Builder().url(requestUrl).header("Authorization", token).get().build();

      // 创建一个Call
      Call call = okHttpClient.newCall(request);
      // 执行请求
      Response response = call.execute();

      InputStream is = response.body().byteStream();
      int len = 0;
      // 设置下载图片存储路径和名称
      File file = new File(downloadPath, "modelId_" + modleId + ".zip");
      FileOutputStream fos = new FileOutputStream(file);
      byte[] buf = new byte[1024];
      while ((len = is.read(buf)) != -1) {
        fos.write(buf, 0, len);
      }
      fos.flush();
      fos.close();
      is.close();

      return true;

    } catch (IOException e) {
      throw new IOException("Inner error " + e);
    }
  }

  /**
   * 登入
   *
   * @param requestUrl
   * @param username
   * @param password
   * @return
   * @throws IOException
   */
  public static Response login(String requestUrl, String username, String password) {

    RequestBody requestBody =
        new FormBody.Builder().add("username", username).add("password", password).build();
    Request request = new Request.Builder().url(requestUrl).post(requestBody).build();

    Response response = null;
    try {
      response = okHttpClient.newCall(request).execute();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return response;
  }
  /**
   * post同步请求
   *
   * @param requestUrl
   * @param requestParam 请求参数：json字符串
   * @return
   * @throws IOException
   */
  public static Response requestPutBySyn(String requestUrl, String requestParam, String token)
          throws IOException {
    // 创建一个请求
    Request request = null;

    try {
      // 不带参数
      if (requestParam == null) {
        request = new Request.Builder().url(requestUrl).header("Authorization", token).build();
      }
      // 带参数
      else {
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(mediaType, requestParam);
        request =
                new Request.Builder()
                        .url(requestUrl)
                        .header("Authorization", token)
                        .put(requestBody)
                        .build();
      }
      // 创建一个Call
      Call call = okHttpClient.newCall(request);
      // 执行请求
      Response response = call.execute();

      return response;

    } catch (IOException e) {
      throw new IOException("Inner error " + e);
    }
  }

}
