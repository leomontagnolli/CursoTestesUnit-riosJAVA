package br.ce.wcaquino.servicos;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.internal.runners.statements.ExpectException;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;

import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exception.FilmesSemEstoqueException;
import br.ce.wcaquino.exception.LocadoraException;
import br.ce.wcaquino.utils.DataUtils;

public class LocacaoServiceTest {
	
	@Rule
	public ErrorCollector error = new ErrorCollector();
	
	private LocacaoService service;
	
	private static int contador;
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	
	
	@Before
	public void setup() {
		service = new LocacaoService();
		contador++;
		System.out.println(contador);
		
	}
	
	
	@Test
	public void testeLocacao() throws Exception {
		//cenario

		Usuario usuario = new Usuario("Leonardo");
		Filme filme = new Filme("Titanic", 2, 5.0);
		//acao
		
		Locacao locacao = service.alugarFilme(usuario, filme);
			error.checkThat(locacao.getValor(), is(equalTo(5.0)));
			error.checkThat(DataUtils.isMesmaData(locacao.getDataLocacao(), new Date()), is(true));
			error.checkThat(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterDataComDiferencaDias(1)), CoreMatchers.is(true));
			
			
		
		
		//verificacao
	}

	
	@Test(expected = FilmesSemEstoqueException.class)
	public void testLocacao_filmeSemEstoque() throws Exception {
		
		//cenario
		Usuario usuario = new Usuario("Leonardo");
		Filme filme = new Filme("Titanic", 0, 5.0);
				
		//acao
				
		Locacao locacao = service.alugarFilme(usuario, filme);
		
	}
	
	

	public void testLocacao_filmeSemEstoque2() {
		
		//cenario
		Usuario usuario = new Usuario("Leonardo");
		Filme filme = new Filme("Titanic", 0, 5.0);
				
		//acao
				
		try {
			service.alugarFilme(usuario, filme);
			Assert.fail("Deveria lançar excecao");
		} catch (Exception e) {
			Assert.assertThat(e.getMessage(), CoreMatchers.is("Sem estoque"));
		}
		
	}
	
	
	@Test
	public void testLocacao_filmeSemEstoque3() throws Exception {
		
		//cenario
		
		Usuario usuario = new Usuario("Leonardo");
		Filme filme = new Filme("Titanic", 0, 5.0);
		
		exception.expect(FilmesSemEstoqueException.class);
		
		
		//acao
				
		Locacao locacao = service.alugarFilme(usuario, filme);
		
		
		
	}
	
	@Test
	public void testLocacaoUsuarioVazio() throws FilmesSemEstoqueException {
		//cenario
		Filme filme = new Filme("Titanic", 1, 5.0);
		
		//acao
		try {
			service.alugarFilme(null, filme);
			Assert.fail();
		}  catch (LocadoraException e) {
			Assert.assertThat(e.getMessage(), CoreMatchers.is("Usuario vazio"));
		}
		
	}
	
	@Test
	public void testLocacaoFVazio() throws FilmesSemEstoqueException, LocadoraException {
		//cenario
		Usuario usuario = new Usuario("Leonardo");
		
		exception.expect(LocadoraException.class);
		
		
		//acao
	
		service.alugarFilme(usuario, null);

		
		
	}
	
}
