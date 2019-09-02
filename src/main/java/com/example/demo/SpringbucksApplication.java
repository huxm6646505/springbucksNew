package com.example.demo;

import com.example.demo.handler.BytesToMoneyConverter;
import com.example.demo.handler.MoneyToBytesConverter;
import com.example.demo.mapper.CoffeeNewMapper;
import com.example.demo.model.CoffeeNew;
import com.example.demo.model.CoffeeNewExample;
import com.example.demo.service.CoffeeNewService;
import com.github.pagehelper.PageInfo;
import io.lettuce.core.ReadFrom;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.RowBounds;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.LettuceClientConfigurationBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.convert.RedisCustomConversions;

import java.util.*;

@SpringBootApplication
@Slf4j
@MapperScan("com.example.demo.mapper")
public class SpringbucksApplication implements ApplicationRunner {
	@Autowired(required = false)
	private CoffeeNewMapper coffeeNewMapper;


	@Autowired(required = false)
	private CoffeeNewService coffeeNewService;

	public static void main(String[] args) {
		SpringApplication.run(SpringbucksApplication.class, args);
	}

	@Bean
	public RedisTemplate<String, CoffeeNew> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
		RedisTemplate<String, CoffeeNew> template = new RedisTemplate<>();
		template.setConnectionFactory(redisConnectionFactory);
		return template;
	}


	//redis lettuce客户端配置
	@Bean
	public LettuceClientConfigurationBuilderCustomizer customizer() {
		return builder -> builder.readFrom(ReadFrom.MASTER_PREFERRED);
	}

	@Bean
	public RedisCustomConversions redisCustomConversions() {
		return new RedisCustomConversions(
				Arrays.asList(new MoneyToBytesConverter(), new BytesToMoneyConverter()));
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		//mybatis generate 生成类与方法
//		generateArtifacts();

		//mybatis 插入数据方法
//		playWithArtifacts();

		//mybatis page分页方法
//		pagehelper();
		redisTest();
	}

//MyBatis 代码生成初始化
	private void generateArtifacts() throws Exception {
		List<String> warnings = new ArrayList<>();
		ConfigurationParser cp = new ConfigurationParser(warnings);
		Configuration config = cp.parseConfiguration(
				this.getClass().getResourceAsStream("/generatorConfig.xml"));
		DefaultShellCallback callback = new DefaultShellCallback(true);
		MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
		myBatisGenerator.generate(null);
	}


	//插入数据
	private void playWithArtifacts() {
		CoffeeNew espresso = new CoffeeNew()
				.withName("espresso")
				.withPrice(Money.of(CurrencyUnit.of("CNY"), 20.0))
				.withCreateTime(new Date())
				.withUpdateTime(new Date());
		coffeeNewMapper.insert(espresso);

		CoffeeNew latte = new CoffeeNew()
				.withName("latte")
				.withPrice(Money.of(CurrencyUnit.of("CNY"), 30.0))
				.withCreateTime(new Date())
				.withUpdateTime(new Date());
		coffeeNewMapper.insert(latte);

		CoffeeNew s = coffeeNewMapper.selectByPrimaryKey(1L);
		log.info("Coffee {}", s);

		CoffeeNewExample example = new CoffeeNewExample();
		example.createCriteria().andNameEqualTo("latte");
		List<CoffeeNew> list = coffeeNewMapper.selectByExample(example);
		list.forEach(e -> log.info("selectByExample: {}", e));
	}

	//Mybatis 分页测试
	private void pagehelper() {
		{
			coffeeNewMapper.findAllWithRowBounds(new RowBounds(1, 3))
					.forEach(c -> log.info("Page(1) Coffee {}", c));
			coffeeNewMapper.findAllWithRowBounds(new RowBounds(2, 3))
					.forEach(c -> log.info("Page(2) Coffee {}", c));

			log.info("===================");

			coffeeNewMapper.findAllWithRowBounds(new RowBounds(1, 0))
					.forEach(c -> log.info("Page(1) Coffee {}", c));

			log.info("===================");

			coffeeNewMapper.findAllWithParam(1, 3)
					.forEach(c -> log.info("Page(1) Coffee {}", c));
			List<CoffeeNew> list = coffeeNewMapper.findAllWithParam(2, 3);
			PageInfo page = new PageInfo(list);
			log.info("PageInfo: {}", page);
		}
	}

	private  void  redisTest(){
		Optional<CoffeeNew> c = coffeeNewService.findOneCoffee("mocha");
		log.info("Coffee {}", c);

		for (int i = 0; i < 5; i++) {
			c = coffeeNewService.findOneCoffee("mocha");
		}

		log.info("Value from Redis: {}", c);
	}


}
