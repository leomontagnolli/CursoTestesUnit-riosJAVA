package br.ce.wcaquino.servicos;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;


public class CalculadoraMockTest {
	
	@Mock
	private Calculadora calcMock;
	
	
	@Spy
	private Calculadora calcSpy;
	
	@Mock
	private EmailService mail;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}
	
	
	@Test
	public void devoMostrarDiferencaEntreSpyEMock() {
		Mockito.when(calcMock.somar(1, 2)).thenReturn(3);
		Mockito.when(calcSpy.somar(1, 2)).thenReturn(3);
		Mockito.doNothing().when(calcSpy).imprimir();
		
		System.out.println("Mock " +calcMock.somar(4, 2));
		System.out.println("Spy " + calcSpy.somar(4, 2));
		
		System.out.println("Mock");
		calcMock.imprimir();
		System.out.println("Spy");
		calcSpy.imprimir();
	}
	
	
	@Test
	public void teste() {
		Calculadora calc = Mockito.mock(Calculadora.class);
		Mockito.when(calc.somar(Mockito.eq(5), Mockito.anyInt())).thenReturn(5);
		
		Assert.assertEquals(5, calc.somar(5, 111111));
	}

}
