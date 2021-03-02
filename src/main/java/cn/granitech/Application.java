package cn.granitech;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

/**
 * @Description:
 * @Author: VDP 思南
 * @Date: 2020/8/19 18:06
 * @Version: 1.0
 */

@SpringBootApplication
@ServletComponentScan  // scans from the package of the annotated class (@WebServlet, @WebFilter, and @WebListener)
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
