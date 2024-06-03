package br.gafardi.rest.test;

import java.util.HashMap;
import java.util.Map;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import br.gafardi.rest.core.BaseTest;
import io.restassured.RestAssured;
import utils.DataUtils;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BarrigaTest extends BaseTest{
	private String TOKEN;
	
	private static String CONTA_NAME = "Conta "+ System.nanoTime();
	
	private static Integer CONTA_ID;
	private static Integer MOV_ID;

	@Before
	public void login() {
		Map<String, String> login = new HashMap<>();
		login.put("email", "df.leirbag@gmail.com");
		login.put("senha", "123456");
		
		TOKEN = RestAssured
		.given()
			.body(login)
		.when()
			.post("/signin")
		.then()
			.statusCode(200).extract().path("token")
		;
	}

	@Test
	public void t01_naoDeveAcessarAPIsemToken() {
		RestAssured
		.given()
		.when()
			.get("/contas")
		.then()
			.statusCode(401)
		;}
	
	@Test
	public void t02_deveIncluirContaComSucesso() {
		CONTA_ID = RestAssured
		.given()
		.body("{\"nome\": \""+CONTA_NAME+"\"}")
			.header("Authorization", "JWT "+TOKEN)
		.when()
			.post("/contas")
		.then()
			.statusCode(201)
			.extract().path("id")
		;
	}
	
	@Test
	public void t03_deveAlterarContaComSucesso() {
		RestAssured
		.given()
		.body("{\"nome\": \""+CONTA_NAME+" alterada\"}")
			.header("Authorization", "JWT "+TOKEN)
			.pathParam("id",CONTA_ID)
		.when()
			.put("/contas/{id}")
		.then()
			.statusCode(200).body("nome", Matchers.is(CONTA_NAME+" alterada"))
		;
	}
	@Test
	public void t04_naoDeveInserirContaComMesmoNome() {
		RestAssured
		.given()
		.body("{\"nome\": \""+CONTA_NAME+" alterada\"}")
			.header("Authorization", "JWT "+TOKEN)
		.when()
			.post("/contas")
		.then()
			.statusCode(400)
			.body("error", Matchers.is("Já existe uma conta com esse nome!"))
		;
	}
	@Test
	public void t05_deveInserirMovimentacaoComSucesso() {
		Movimentacao mov = new Movimentacao();
		mov.setConta_id(CONTA_ID);
//		mov.getUsuario_id();
		mov.setDescricao("Descricao movimentacao");
		mov.setEnvolvido("envolvido na movt");
		mov.setTipo("REC");
		mov.setData_transacao("01/01/2010");
		mov.setData_pagamento("01/01/2010");
		mov.setValor(100f);
		mov.setStatus(true);

				
		MOV_ID = RestAssured
		.given()
			.header("Authorization", "JWT "+TOKEN)
			.body(mov)
		.when()
			.post("/transacoes")
		.then()
			.statusCode(201)
			.extract().path("id")
		;
	}
	
	@Test
	public void t06_deveValidarCamposObrigatoriosNaMovimentacao() {

		RestAssured
		.given()
			.header("Authorization", "JWT "+TOKEN)
			.body("{}")
		.when()
			.post("/transacoes")
		.then()
			.statusCode(400)
			.body("$", Matchers.hasSize(8))
			.body("msg", Matchers.hasItems(
					"Data da Movimentação é obrigatório",
					"Data do pagamento é obrigatório",
					"Descrição é obrigatório",
					"Interessado é obrigatório",
					"Valor é obrigatório",
					"Valor deve ser um número",
					"Conta é obrigatório",
					"Situação é obrigatório"
					))
		;
	}
	
	@Test
	public void t07_naoDeveInserirMovimentacaoFutura() {
		Movimentacao mov = new Movimentacao();
		mov.setConta_id(CONTA_ID);
//		mov.getUsuario_id();
		mov.setDescricao("Descricao movimentacao");
		mov.setEnvolvido("envolvido na movt");
		mov.setTipo("REC");
		mov.setData_transacao("01/01/2045");
		mov.setData_pagamento("01/01/2045");
		mov.setValor(100f);
		mov.setStatus(true);

				
		RestAssured
		.given()
			.header("Authorization", "JWT "+TOKEN)
			.body(mov)
		.when()
			.post("/transacoes")
		.then()
			.statusCode(400)
			.body("msg", Matchers.hasItem("Data da Movimentação deve ser menor ou igual à data atual"))
		;
	}
	
	@Test
	public void t08_naoDeveExcluirContaComMovimentacao() {
		RestAssured
		.given()
			.header("Authorization", "JWT "+TOKEN)
		.when()
			.delete("/contas/"+CONTA_ID)
		.then()
			.statusCode(500)
			.body("constraint", Matchers.is("transacoes_conta_id_foreign"))
		;
	}
	
	@Test
	public void t09_deveCalcularSaldoContas() {
		RestAssured
		.given()
			.header("Authorization", "JWT "+TOKEN)
		.when()
			.get("/saldo")
		.then()
			.statusCode(200)
			.body("find{it.conta_id == "+CONTA_ID+"}.saldo", Matchers.is("100.00"))
		;
	}
	
	@Test
	public void t10_deveRemoverMovimentacao() {
		RestAssured
		.given()
			.header("Authorization", "JWT "+TOKEN)
		.when()
			.delete("/transacoes/"+MOV_ID)
		.then()
			.statusCode(204)
		;
	}
	
	
}
