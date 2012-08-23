package org.orinocoX509.service.impl.extensions;

import java.io.StringWriter;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class VelocitySupport 
{
	private VelocityEngine velocityEngine;
	
	private static final Logger log = LoggerFactory.getLogger(VelocitySupport.class);
	
	public VelocitySupport()
	{
		this.velocityEngine = new VelocityEngine();
		velocityEngine.init();
	}
	
	public String applyTemplate(String template, Map<String,String> templateValues,String logDescription)
	{
		if (templateValues == null)
		{
			log.debug("No values for template " + template + "were given. No change was done");
			return(template);
		}
		
		VelocityContext context = new VelocityContext();
		for(Entry<String, String> entry : templateValues.entrySet())
		{
			context.put(entry.getKey(), entry.getValue());
		}
		
		StringWriter outputParser = new StringWriter();
		velocityEngine.evaluate(context, outputParser, logDescription, template);
		log.debug("Result for template " + template + " was " + outputParser.toString());
		return(outputParser.toString());
	}
}
