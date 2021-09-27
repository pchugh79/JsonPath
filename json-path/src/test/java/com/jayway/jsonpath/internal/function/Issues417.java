package com.jayway.jsonpath.internal.function;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import org.junit.Assert;
import org.junit.Test;

public class Issues417 {

	@Test
	public void test1() {
		String json = "{ \"service\": \"99282-we103-1712\", \"data\": { \"DV-Number\": \"0000023\", \"DV-UpdateTime\": null, \"DV-Application.OverrideDecision\": null }}";
		JsonPath query = JsonPath.compile("$.data.['DV-UpdateTime']");
		Configuration config = Configuration.builder()
						//.options(Option.SUPPRESS_EXCEPTIONS)
						.build();
		DocumentContext documentContext = JsonPath.using(config).parse(json);
		Assert.assertNull(documentContext.read(query));
	}

	@Test
	public void testWithJacksonParserWithIgnoreNullValues() throws JsonProcessingException {
		Configuration config = Configuration.builder()
						.options(Option.SUPPRESS_EXCEPTIONS)
						.build();

		String json = "{\"b\":{\"c\":null}}";
		System.out.println(json);
		DocumentContext documentContext = JsonPath.using(config).parse(json);
		JsonPath query = JsonPath.compile("$.b.c.d.e.f");
		Assert.assertNull(documentContext.read(query));
		Assert.assertNull(documentContext.map(query, (object, configuration) -> object));


		System.out.println("executing with Ignoring Null Values.");
		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		JacksonJsonProvider jsonProvider = new JacksonJsonProvider(mapper);
		JacksonMappingProvider jacksonMappingProvider = new JacksonMappingProvider(mapper);
		Configuration.ConfigurationBuilder valueConfigurationBuilder = new Configuration.ConfigurationBuilder();
		valueConfigurationBuilder.jsonProvider(jsonProvider);
		valueConfigurationBuilder.mappingProvider(jacksonMappingProvider);
		valueConfigurationBuilder.options(Option.SUPPRESS_EXCEPTIONS);
		config = valueConfigurationBuilder.build();

		A a = new A();
		a.setB(new B());
		json = mapper.writeValueAsString(a);
		System.out.println(json);
		documentContext = JsonPath.using(config).parse(json);
		Assert.assertNull(documentContext.read(query));
		DocumentContext mappedDocumentContext = documentContext.map(query, (object, configuration) -> object);
		Assert.assertNotNull(mappedDocumentContext);
	}


	public static class A {
		private B b;

		public B getB() {
			return b;
		}

		public void setB(B b) {
			this.b = b;
		}
	}

	public static class B {
		private C c;

		public C getC() {
			return c;
		}

		public void setC(C c) {
			this.c = c;
		}
	}

	public static class C {
		String cName;

		public String getcName() {
			return cName;
		}

		public void setcName(String cName) {
			this.cName = cName;
		}
	}
}
