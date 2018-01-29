package io.zdp.wallet.desktop.ui.common;

import java.util.Locale;

import javax.annotation.Resource;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

@Service
public class I18n {

	@Resource(name = "messageSource")
	private MessageSource messageSource;

	public String get(String key) {
		return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
	}

	public Locale getUserLocale() {
		return Locale.getDefault();
	}

}
