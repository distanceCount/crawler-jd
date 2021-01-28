package cn.crawler.jd.service;

import cn.crawler.jd.pojo.Item;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName: Itemservice
 * @Description: TODO
 * @author: yang
 * @date: 2021/1/27  15:14
 */
public interface Itemservice {
    /**
     * 保存商品
     * @param item
     */
    void save(Item item);

    /**
     * 查询商品
     * @param item
     * @return
     */
    List<Item> findAll(Item item);
}
