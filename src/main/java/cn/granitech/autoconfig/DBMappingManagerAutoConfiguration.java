package cn.granitech.autoconfig;

import cn.granitech.variantorm.dbmapping.DBMappingManager;
import cn.granitech.variantorm.persistence.PersistenceManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * @Description:
 * @Author: VDP 思南
 * @Date: 2020/8/21 13:49
 * @Version: 1.0
 */

@Configuration
@EnableConfigurationProperties
public class DBMappingManagerAutoConfiguration {

    @Autowired
    DataSource dataSource;

    @Autowired
    PersistenceManager persistenceManager;

    @Bean
    DBMappingManager dBMappingManager() {
        return new DBMappingManager(dataSource, persistenceManager.getMetadataManager());
    }

}
