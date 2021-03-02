package cn.granitech.autoconfig;

import cn.granitech.variantorm.dbmapping.DBMappingManager;
import cn.granitech.variantorm.persistence.PersistenceManager;
import cn.granitech.variantorm.persistence.impl.PersistenceManagerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * @Description:
 * @Author: VDP 思南
 * @Date: 2020/8/21 12:28
 * @Version: 1.0
 */

@Configuration
@EnableConfigurationProperties
public class PersistenceManagerAutoConfiguration {

    @Autowired
    DataSource dataSource;

    @Bean
    PersistenceManager persistenceManager() {
        return new PersistenceManagerImpl(dataSource);
    }

}
