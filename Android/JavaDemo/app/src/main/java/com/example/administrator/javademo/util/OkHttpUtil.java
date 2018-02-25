package com.example.administrator.javademo.util;


import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.administrator.javademo.listener.OnDownloadListener;
import com.example.administrator.javademo.util.uploadfile.ProgressHelper;
import com.example.administrator.javademo.util.uploadfile.ProgressUIListener;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 *
 * Date: 2016-03-07
 * Time: 21:45
 */
public class OkHttpUtil {

    private static OkHttpClient mClient;

    static {
        if (mClient == null) {
            mClient = new OkHttpClient.Builder()
                    .connectTimeout(AppConstants.TIME_OUT, TimeUnit.SECONDS)
                    .writeTimeout(AppConstants.TIME_OUT, TimeUnit.SECONDS)
                    .readTimeout(AppConstants.TIME_OUT, TimeUnit.SECONDS)
                    .build();
        }
    }

    /**
     * 执行Get请求
     *
     * @param url
     * @param callback
     */
    public static void get(String url, Callback callback) throws IOException {
        Request request = new Request.Builder().url(url).get().build();
        Call call = mClient.newCall(request);
        call.enqueue(callback);
    }

    /**
     * 执行post
     *
     * @param url
     * @param params
     * @param callback
     * @throws IOException
     */
    public static void postJson(String url, Map<String, Object> params, Callback callback) throws IOException {
        FormBody formBody = getBuilder(params, new FormBody.Builder()).build();
        Request request = new Request.Builder().url(url).post(formBody).build();
        Call call = mClient.newCall(request);
        call.enqueue(callback);
    }

    public static void postString(String url, Map<String, String> params, Callback callback) throws IOException {
        FormBody formBody = getBuilderString(params, new FormBody.Builder()).build();
        Request request = new Request.Builder().url(url).post(formBody).build();
        Call call = mClient.newCall(request);
        call.enqueue(callback);
    }

    /**
     * 提交png
     */
    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");

    public static void postPNG(String url, Map<String, Object> params, File file, String cookie ,Callback callback) throws IOException {
        Request request = new Request.Builder().url(url).
                post(getBuilder(params, new FormBody.Builder()).build()).
                post(RequestBody.create(MEDIA_TYPE_PNG, file)).
                addHeader("cookie", cookie).build();
        Call call = mClient.newCall(request);
        call.enqueue(callback);
    }

    /**
     * 上传文件(包括视频音频)
     * application/octet-stream  二进制流
     * text/x-markdown; charset=utf-8
     */
    public static void upLoadFile(String url, File file,String fileName,String type,Callback callback) throws IOException {
        RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream") , file);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file" , fileName , fileBody)
                .addFormDataPart("type", type)
                .build();
        Request request = new Request.Builder().url(url).post(requestBody).build();
        mClient.newCall(request).enqueue(callback);
    }

    /**
     * 多文件上传
     * @param callback
     * @throws IOException
     */
    public static void upLoadFileMuti(String url,List<String> filePaths,String info,String type,String userId, Callback callback) throws IOException {
        //创建MultipartBody.Builder，用于添加请求的数据
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.addFormDataPart("type", type+"")
                .addFormDataPart("info",info)
                .addFormDataPart("user",userId+"");
        for (int i = 0; i < filePaths.size(); i++) { //对文件进行遍历
            File file = new File(filePaths.get(i)); //生成文件
            builder.addFormDataPart( //给Builder添加上传的文件
                    "files",  //请求的名字
                    file.getName(), //文件的文字，服务器端用来解析的
                    RequestBody.create(MediaType.parse("application/octet-stream"), file) //创建RequestBody，把上传的文件放入
            );
        }
        MultipartBody build = builder.build();
        Request request = new Request.Builder().url(url)
                .post(build).build();
        Call call = mClient.newCall(request) ;
        call.enqueue(callback);
    }

    /**
     * 文件上传带进度条
     * @param url
     * @param file
     * @param listener
     * @param callback
     */
    public static void upLoadFileProgress(String url, File file, String fileName, ProgressUIListener listener,Callback callback){
        Request.Builder builder = new Request.Builder();
        builder.url(url);
        MultipartBody build = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("fileName", fileName)
                .addFormDataPart("file", file.getName(), RequestBody.create(null, file))
                .build();
        RequestBody requestBody = ProgressHelper.withProgress(build,listener);
        builder.post(requestBody);
        mClient.newCall(builder.build()).enqueue(callback);
    }

    /**
     * 装载formBody
     *
     * @param params
     * @param builder
     * @return
     */
    private static FormBody.Builder getBuilder(Map<String, Object> params, FormBody.Builder builder) {
        Gson gson = new Gson();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            if (entry.getValue() instanceof String) {
                builder.add(entry.getKey(), String.valueOf(entry.getValue()));
            } else {
                builder.add(entry.getKey(), gson.toJson(entry.getValue()));
            }
        }
        return builder;
    }


    /**
     * 装载formBody
     *
     * @param params
     * @param builder
     * @return
     */
    private static FormBody.Builder getBuilderString(Map<String, String> params, FormBody.Builder builder) {
        for (Map.Entry<String, String> entry : params.entrySet()) {
            builder.add(entry.getKey(), entry.getValue());
        }
        return builder;
    }

    public static void download(String url, Callback Callback) throws IOException{
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = mClient.newCall(request);
        call.enqueue(Callback);
    }

    /**
     * 加载网络图片
     * @param url
     * @param view
     * @throws IOException
     */
    public static void getPic(String url, final View view, final Activity activity) throws IOException {
        get(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response!=null && response.code()==200){
                    InputStream is = response.body().byteStream();
                    final Bitmap bitmap = BitmapFactory.decodeStream(is);
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (view instanceof ImageView && bitmap!=null) {
                                ImageView iv = (ImageView) view;
                                iv.setImageBitmap(bitmap);
                            }/*else if (view instanceof MyImageView){
                                MyImageView iv = (MyImageView) view;
                                iv.setImageBitmap(bitmap);
                            }*/
                        }
                    });
                }
            }
        });
    }

    public static String getResult(Response response) throws IOException {

        if (response!=null && !"null".equals(response) && response.code() == 200)
            return response.body().string();
        return null;
    }

    /**
     * 从服务器下载文件
     */
    public static void downLoad(final String urlPath, final String filePath, final String fileName, ProgressBar progressBar) {
        progressBar.setMax(100);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(urlPath);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setReadTimeout(5000);
                    con.setConnectTimeout(5000);
                    con.setRequestProperty("Charset", "utf-8");
                    con.setRequestMethod("GET");
                    int length = con.getContentLength();
                    if (con.getResponseCode() == 200) {
                        InputStream is = con.getInputStream();//获取输入流
                        FileOutputStream fileOutputStream = null;//文件输出流
                        if (is != null) {
//                    String SDPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/JavaDemo";
                            File file = new File(filePath, fileName);
                            fileOutputStream = new FileOutputStream(file);//指定文件保存路径，代码看下一步
                            byte[] buf = new byte[1024];
                            int ch;
                            long count=0;
                            while ((ch = is.read(buf)) != -1) {
                                fileOutputStream.write(buf, 0, ch);//将获取到的流写入文件中
                                count += ch;
                                LogUtil.e("radish---progress---",(int)(((float)count / length) * 100)+"");
                            }
                        }
                        if (fileOutputStream != null) {
                            fileOutputStream.flush();
                            fileOutputStream.close();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    private int mPrePosition;

    public long getContentLength(String downloadUrl) throws IOException {
        Request request =  new Request.Builder().url(downloadUrl).build();
        Response response = mClient.newCall(request).execute();
        if(response != null && response.isSuccessful()){
            long contentLength = response.body().contentLength();
            response.body().close();
            return contentLength;
        }
        return 0;
    }

    private boolean isCanceled = false;
    private boolean isPaused = false;

    /**
     * 实现断点续传且带进度条
     * @param downloadUrl  //url地址
     * @param filePath  //文件存储位置
     * @throws IOException
     */
    public void download(final String downloadUrl,final String filePath, final OnDownloadListener downloadListener){

        new Thread(){
            @Override
            public void run() {
                try {
                    long downloadedLength = 0;
                    String directory = FileUtil.getDirectoryNameByPath(filePath);
                    File dirFile = new File(directory);
                    if (!dirFile.exists()){
                        dirFile.mkdirs();
                    }
                    File file = new File(filePath);
                    if(file.exists()){
                        downloadedLength = file.length();
                    }
                    long contentLength = 0;
                    contentLength = getContentLength(downloadUrl);
                    if(contentLength == 0){
                        if (downloadListener != null){
                            downloadListener.fail();
                        }
                    }else if(contentLength == downloadedLength){
                        if (downloadListener != null){
                            downloadListener.success();
                        }
                    }
                    Request request = new Request.Builder()
                            .addHeader("RANGE","bytes=" + downloadedLength + "-")//断点下载，指定从哪个字节开始下载
                            .url(downloadUrl)
                            .build();
                    Response response = mClient.newCall(request).execute();
                    if(response != null) {
                        InputStream is = response.body().byteStream();
                        RandomAccessFile saveFile = new RandomAccessFile(file, "rw");
                        saveFile.seek(downloadedLength);//跳过已下载的字节
                        byte[] b = new byte[1024];
                        int total = 0;
                        int len;
                        while ((len = is.read(b)) != -1) {
                            if (isCanceled) {
                                if (downloadListener != null){
                                    downloadListener.canceled();
                                    break;
                                }
                            } else if (isPaused) {
                                if (downloadListener != null){
                                    downloadListener.paused();
                                    break;
                                }
                            } else {
                                total += len;
                                saveFile.write(b, 0, len);
                                int progress = (int) ((total + downloadedLength) * 100 / contentLength);
                                if (downloadListener != null && progress != mPrePosition){
                                    mPrePosition = progress;
                                    downloadListener.downloading(progress);
                                }
                            }
                        }
                        if (downloadListener != null && !isCanceled && !isPaused){
                            downloadListener.success();
                        }
                        response.body().close();
                    }
                } catch (Exception e) {
                    if (downloadListener != null){
                        downloadListener.fail();
                    }
                    e.printStackTrace();
                }

            }
        }.start();



    }

    public void setPaused(boolean isPaused){
        this.isPaused = isPaused;
    }
    public void setCanceled(boolean isCanceled){
        this.isCanceled = isCanceled;
    }
}

