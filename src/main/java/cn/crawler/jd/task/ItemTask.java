package cn.crawler.jd.task;

import cn.crawler.jd.pojo.Item;
import cn.crawler.jd.service.Itemservice;
import cn.crawler.jd.utils.HttpUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * @ClassName: ItemTask
 * @Description: TODO
 * @author: yang
 * @date: 2021/1/28  8:47
 */
@Component
public class ItemTask {
    @Autowired
    private HttpUtils httpUtils;
    @Autowired
    private Itemservice itemservice;

    public static final ObjectMapper MAPPER = new ObjectMapper();

    @Scheduled(fixedDelay = 1000*100)
    public void process() throws Exception{
        //分析页面发现访问的地址,页码page从1开始,下一页page+2
        String url = "https://search.jd.com/Search?keyword=%E6%89%8B%E6%9C%BA&enc=utf-8&qrst=1&rt=1&stop=1&vt=2&cid2=653&cid3=655&s=5760&click=0&page=";

        //遍历执行,获取所有的数据
        for (int i = 0; i < 10; i+=2) {
            //发起请求进行访问,获取页面数据,先访问第一页
            String html = this.httpUtils.getHtml(url+i);

            //解析页面数据,保存数据到数据库
            this.parseHtml(html);
        }
        System.out.println("执行完成");
    }

    //解析页面,并把数据保存到数据库中
    private void parseHtml(String html) throws JsonProcessingException {
        //使用jsoup解析页面
        Document document = Jsoup.parse(html);
        //获取商品数据
        Elements spus = document.select("dev#J_goodsList > ul > li");
        //遍历spu数据
        for (Element spuEle : spus) {
            //获取商品spu
            long spuId = Long.parseLong(spuEle.attr("data-spu"));
            //获取商品sku数据
            Elements skus = spuEle.select("li.ps-item img");
            for (Element skuEle : skus) {
                long skuId = Long.parseLong(skuEle.attr("data-sku"));
                //判断商品是否被抓取过,根据sku判断
                Item param = new Item();
                param.setSku(skuId);
                List<Item> list = this.itemservice.findAll(param);
                //判断是否查询到结果
                if (list.size()>0){
                    //如果有,则代表已经下载,跳过此次循环
                    continue;
                }
                //保存商品数据,声明商品对象
                Item item = new Item();
                //商品spu
                item.setSpu(spuId);
                //商品sku
                item.setSku(skuId);

                //商品详情地址
                item.setUrl("https://item.jd.com/" + skuId + ".html");
                //商品创建时间
                item.setCreated(new Date());
                //商品更新时间
                item.setUpdated(item.getCreated());

                //商品标题
                String itemHtml = this.httpUtils.getHtml(item.getUrl());
                String title = Jsoup.parse(itemHtml).select("div.sku-name").text();
                item.setTitle(title);
                //商品价格
                String priceUrl = "https://p.3.cn/prices/mgets?skuIds=J_"+skuId;
                String priceJson = this.httpUtils.getHtml(priceUrl);
                //解析json数据获取商品价格
                double price = MAPPER.readTree(priceJson).get(0).get("p").asDouble();
                item.setPrice(price);

                //获取图片地址
                String pic = "https:" + skuEle.attr("data-lazy-img").replace("/n9/","/n1/");
                //下载图片
                String picName = this.httpUtils.getImage(pic);
                item.setPic(picName);

                //保存商品数据
                this.itemservice.save(item);
            }
        }
    }
}
