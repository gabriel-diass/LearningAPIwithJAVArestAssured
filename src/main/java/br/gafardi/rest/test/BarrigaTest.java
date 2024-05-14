package br.gafardi.rest.test;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import br.gafardi.rest.core.BaseTest;
import io.restassured.RestAssured;

public class BarrigaTest extends BaseTest{

	@Test
	public void naoDeveAcessarAPIsemToken() {
		RestAssured
		.given()
		.when()
			.get("/contas")
		.then()
			.statusCode(401)
		;}
	
	@Test
	public void deveIncluirContaComSucesso() {
		Map<String, String> login = new HashMap<>();
		login.put("email", "df.leirbag@gmail.com");
		login.put("senha", "123456");
		
		String token = RestAssured
		.given()
			.body(login)
		.when()
			.post("/signin")
		.then()
			.statusCode(200).extract().path("token")
		;
		
		RestAssured
		.given()
		.body("{\"nome\": \"conta qualuqer\"} ")
			.header("Authorization", "JWT "+token)
		.when()
			.post("/contas")
		.then()
			.statusCode(201)
		;
	}
	
	
}
