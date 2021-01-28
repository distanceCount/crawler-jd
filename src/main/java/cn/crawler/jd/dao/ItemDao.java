package cn.crawler.jd.dao;

import cn.crawler.jd.pojo.Item;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @ClassName: ItemDao
 * @Description: TODO
 * @author: yang
 * @date: 2021/1/27  15:12
 */
public interface ItemDao extends JpaRepository<Item,Long> {

}
