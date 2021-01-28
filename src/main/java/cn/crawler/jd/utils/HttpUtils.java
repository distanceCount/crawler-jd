package cn.crawler.jd.utils;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

/**
 * @ClassName: HttpUtils
 * @Description: TODO
 * @author: yang
 * @date: 2021/1/27  15:58
 */
@Component
public class HttpUtils {
    private PoolingHttpClientConnectionManager cm;

    public HttpUtils(){
        this.cm = new PoolingHttpClientConnectionManager();

        //设置最大连接数
        cm.setMaxTotal(100);
        //设置每个主机的并发数
        cm.setDefaultMaxPerRoute(20);
    }

    //获取内容
    public String getHtml(String url){
        //获取httpClient对象
        CloseableHttpClient httpclient = HttpClients.custom().setConnectionManager(cm).build();
        //声明httpGet请求对象
        HttpGet httpGet = new HttpGet(url);
        //设置请求参数RequestConfig
        httpGet.setConfig(this.getConfig());

        CloseableHttpResponse response = null;
        try {
            //使用httpClient发起请求,返回response
            response = httpclient.execute(httpGet);
            //解析response返回数据
            if (response.getStatusLine().getStatusCode() == 200){
                String html = "";
                // 如果response。getEntity获取的结果是空，在执行EntityUtils.toString会报错
                // 需要对Entity进行非空的判断
                if (response.getEntity() != null){
                    html = EntityUtils.toString(response.getEntity(),"utf-8");
                }
                return html;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                //关闭连接
                if(response != null) {
                    response.close();
                }
                //httpclient不能关闭,现在使用的是连接管理器
                // httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    //获取图片
    public String getImage(String url) {
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(cm).build();
        //声明httpGet请求对象
        HttpGet httpGet = new HttpGet(url);
        //设置请求参数requestConfig
        httpGet.setConfig(this.getConfig());

        CloseableHttpResponse response = null;
        try {
            //使用httpClient发起请求,返回response
            response = httpClient.execute(httpGet);
            //解析response下载图片
            if (response.getStatusLine().getStatusCode() == 200 ){
                //获取文件类型
                String extName = url.substring(url.lastIndexOf("."));
                //使用uuid生成图片名
                String imageName = UUID.randomUUID().toString() + extName;
                //声明输出的文件
                OutputStream outputStream =
                        new FileOutputStream(new File("D:\\images" + imageName));
                //使用响应体输出文件
                response.getEntity().writeTo(outputStream);
                //返回生成的图片名
                return imageName;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (response != null){
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private RequestConfig getConfig() {
        RequestConfig config =
                RequestConfig.custom().setConnectTimeout(1000)//设置创建连接的超时时间
                            .setConnectionRequestTimeout(500)//设置回去连接的超时时间
                            .setSocketTimeout(10000)//设置连接的超时时间
                            .build();

        return config;
    }


}
