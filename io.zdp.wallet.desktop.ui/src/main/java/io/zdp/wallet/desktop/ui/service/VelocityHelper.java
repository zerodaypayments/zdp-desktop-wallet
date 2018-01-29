package io.zdp.wallet.desktop.ui.service;

import java.io.StringWriter;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.springframework.stereotype.Service;

@Service
public class VelocityHelper {

	@PostConstruct
	public void init() {

		Properties prop = new Properties();
		prop.setProperty("resource.loader", "class");
		prop.setProperty("class.resource.loader.class",
				"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");

		Velocity.init(prop);
	}

	public String process(Map<String, Object> ctx, String templateName) {

		VelocityContext context = new VelocityContext(ctx);

		Template template = Velocity.getTemplate(templateName);
		StringWriter sw = new StringWriter();

		template.merge(context, sw);

		return sw.toString();

	}

}
