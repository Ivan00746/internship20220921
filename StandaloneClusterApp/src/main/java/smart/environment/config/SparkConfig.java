package smart.environment.config;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
@PropertySource("classpath:application.properties")
public class SparkConfig {

    @Value("${app.name:spark-sprint-boot}")
    private String appName;

    @Value("${master.uri:local}")
    private String masterUri;

    @Bean
    public JavaSparkContext javaSparkContext() {
        return new JavaSparkContext(new SparkConf()
                .setAppName(appName)
                .setMaster(masterUri)
                .set("spark.driver.host", "127.0.0.1")
//                .set("spark.driver.host", "172.17.0.3")
//                .set("spark.driver.cores", "4")
//                .set("spark.driver.memory", "4g")
                .set("spark.executor.cores", "2")
                .set("spark.executor.memory", "2g")
                .set("spark.ui.port", "9191"));
    }

    @Bean
    public SparkSession sparkSession() {
        SparkSession sparkSession = SparkSession
                .builder()
                .sparkContext(javaSparkContext().sc())
                .appName("Cluster default implementation of the Spark facilities")
                .getOrCreate();
        sparkSession.sparkContext().setLogLevel("info");
        return sparkSession;
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}