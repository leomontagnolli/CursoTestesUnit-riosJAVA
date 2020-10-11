package br.ce.wcaquino.servicos;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import br.ce.wcaquino.exception.NaoPodeDividirPorZeroException;


public class CalculadoraTest {
	
	private Calculadora calc;
	
	@Before
	public void instacia() {
		calc = new Calculadora();
	}
	
	@Test
	public void deveSomarDoisValores() {
		//cenario
		int a = 3;
		int b = 5;
		
		
		//acao
		int resultado = calc.somar(a,b);
		
		//verificacao
		Assert.assertEquals(8, resultado);
	}
	
	@Test
	public void deveSubtrairDoisValors() {
		//cenario
		int a = 8;
		int b = 5;


		// acao
		int resultado = calc.subtrair(a, b);
		
		//verfi
		Assert.assertEquals(3, resultado);
				
	}
	
	@Test
	public void deveDividirDoisValores() throws NaoPodeDividirPorZeroException {
		//cenario
		int a = 6;
		int b = 3;

		
		//acao
		int resultado = calc.dividir(a,b);
		
		//verificacao
		Assert.assertEquals(2, resultado);
	}
	
	@Test(expected = NaoPodeDividirPorZeroException.class)
	public void deveLancarExcecaoAodividirPor0() throws NaoPodeDividirPorZeroException {
		int a = 10;
		int b = 0;
		
		calc.dividir(a,b);
		
	}

}
