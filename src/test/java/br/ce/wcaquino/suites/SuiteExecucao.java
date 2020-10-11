package br.ce.wcaquino.suites;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import br.ce.wcaquino.servicos.AssertTest;
import br.ce.wcaquino.servicos.CalculadoraTest;
import br.ce.wcaquino.servicos.CalculoValorLocacaoTest;
import br.ce.wcaquino.servicos.LocacaoServiceTest;
import br.ce.wcaquino.servicos.OrdemTest;

//@RunWith(Suite.class)
@SuiteClasses({
	CalculadoraTest.class,
	LocacaoServiceTest.class,
	AssertTest.class,
	CalculoValorLocacaoTest.class,
	OrdemTest.class
})
public class SuiteExecucao {
	

}
