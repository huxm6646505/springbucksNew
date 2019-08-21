package com.example.demo;

import com.example.demo.mapper.CoffeeNewMapper;
import com.example.demo.model.CoffeeNew;
import com.example.demo.model.CoffeeNewExample;
import lombok.extern.slf4j.Slf4j;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SpringBootApplication
@Slf4j
@MapperScan("com.example.demo.mapper")
public class SpringbucksApplication implements ApplicationRunner {
	@Autowired(required = false)
	private CoffeeNewMapper coffeeNewMapper;


	public static void main(String[] args) {
		SpringApplication.run(SpringbucksApplication.class, args);
	}



	@Override
	public void run(ApplicationArguments args) throws Exception {
//		generateArtifacts();

		playWithArtifacts();


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
}
