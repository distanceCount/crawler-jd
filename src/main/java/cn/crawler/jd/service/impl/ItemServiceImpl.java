package cn.crawler.jd.service.impl;

import cn.crawler.jd.dao.ItemDao;
import cn.crawler.jd.pojo.Item;
import cn.crawler.jd.service.Itemservice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName: ItemServiceImpl
 * @Description: TODO
 * @author: yang
 * @date: 2021/1/27  15:20
 */
@Service
public class ItemServiceImpl implements Itemservice {
    @Autowired
    private ItemDao itemDao;

    @Override
    public void save(Item item) {
        itemDao.save(item);
    }

    @Override
    public List<Item> findAll(Item item) {
        //查询条件
        Example example = Example.of(item);
        //查询
        List list = itemDao.findAll(example);
        //返回
        return list;
    }
}
