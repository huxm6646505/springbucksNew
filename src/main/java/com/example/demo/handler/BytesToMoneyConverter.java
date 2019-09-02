package com.example.demo.handler;

import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.springframework.core.convert.converter.Converter;

import java.nio.charset.StandardCharsets;

/**
 * Created by huxm on 2019/8/26
 */
public class BytesToMoneyConverter implements Converter<byte[], Money> {
    @Override
    public Money convert(byte[] source) {
        String value = new String(source, StandardCharsets.UTF_8);
        return Money.ofMinor(CurrencyUnit.of("CNY"), Long.parseLong(value));
    }
}
