package net.devkhan.spring.sample.configuration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.core.io.Resource;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by KHAN on 2015-08-03.
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories("net.devkhan.spring.**.repository")
@ComponentScan(
		basePackages = { "net.devkhan.spring.sample" },
		excludeFilters = @ComponentScan.Filter(value={Configuration.class}, type = FilterType.ANNOTATION)
)
public class SampleConfiguration {

	@Bean
	public Properties repoProps(@Value("classpath:repository.properties") Resource propFile)
			throws IOException {
		Properties props = new Properties();
		props.load(propFile.getInputStream());
		return props;
	}

	@Bean
	public DataSource restDataSource(
			@Value("#{repoProps['jdbc.url']}") String jdbcUrl,
			@Value("#{repoProps['jdbc.driverClassName']}") String jdbcDriverClassName,
			@Value("#{repoProps['jdbc.username']}") String jdbcUsername,
			@Value("#{repoProps['jdbc.password']}") String jdbcPassword) {

		HikariConfig hikariConfig = new HikariConfig();
		hikariConfig.setJdbcUrl(jdbcUrl);
		hikariConfig.setDriverClassName(jdbcDriverClassName);
		hikariConfig.setUsername(jdbcUsername);
		hikariConfig.setPassword(jdbcPassword);

		hikariConfig.setMaximumPoolSize(5);
		hikariConfig.setConnectionTestQuery("SELECT 1");
		hikariConfig.setPoolName("springHikariCP");
		hikariConfig.setIdleTimeout(30000);
		hikariConfig.setMaxLifetime(30000);

		hikariConfig.addDataSourceProperty("dataSource.cachePrepStmts", "true");
		hikariConfig.addDataSourceProperty("dataSource.prepStmtCacheSize", "250");
		hikariConfig.addDataSourceProperty("dataSource.prepStmtCacheSqlLimit", "2048");
		hikariConfig.addDataSourceProperty("dataSource.useServerPrepStmts", "true");

		return new HikariDataSource(hikariConfig);
	}

	@Bean
	@Autowired
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(
			DataSource dataSource,
			@Value("#{repoProps['scanPackages']}") String[] packages,
			@Value("#{repoProps['hibernate.hbm2ddl.auto']}") String hbm2ddlAuto,
			@Value("#{repoProps['hibernate.dialect']}") String dialect,
			@Value("#{repoProps['hibernate.show_sql']}") String showSql,
			@Value("#{repoProps['hibernate.format_sql']}") String formatSql,
			JpaVendorAdapter jpaVendorAdapter
	) {
		LocalContainerEntityManagerFactoryBean sessionFactory = new LocalContainerEntityManagerFactoryBean();
		sessionFactory.setDataSource(dataSource);
		sessionFactory.setPackagesToScan(packages);
		sessionFactory.setJpaVendorAdapter(jpaVendorAdapter);
		sessionFactory.setJpaProperties(hibernateProperties(hbm2ddlAuto, dialect, showSql, formatSql));

		return sessionFactory;
	}

	@Bean
	public HibernateJpaVendorAdapter jpaVendorAdapter(
			@Value("#{repoProps['hibernate.dialect']}") String dialect,
			@Value("#{repoProps['hibernate.show_sql']}") boolean showSql) {
		HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
		jpaVendorAdapter.setDatabasePlatform(dialect);
		jpaVendorAdapter.setShowSql(showSql);
		jpaVendorAdapter.setGenerateDdl(true);
		return jpaVendorAdapter;
	}

	@Bean
	@Autowired
	public JpaTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
		JpaTransactionManager txManager = new JpaTransactionManager();
		txManager.setEntityManagerFactory(entityManagerFactory);
		return txManager;
	}

	@Bean
	public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
		return new PersistenceExceptionTranslationPostProcessor();
	}

	Properties hibernateProperties(String hbm2ddlAuto, String dialect, String showSql, String formatSql) {
		return new Properties() {
			{
				setProperty("hibernate.hbm2ddl.auto", hbm2ddlAuto);
				setProperty("hibernate.dialect", dialect);
				setProperty("hibernate.show_sql", showSql);
				setProperty("hibernate.format_sql", formatSql);
				setProperty("hibernate.globally_quoted_identifiers", Boolean.TRUE.toString());
				setProperty("hibernate.generate_statistics", Boolean.TRUE.toString());
				setProperty("hibernate.cache.use_structured_entire", Boolean.TRUE.toString());
			}
		};
	}

}
