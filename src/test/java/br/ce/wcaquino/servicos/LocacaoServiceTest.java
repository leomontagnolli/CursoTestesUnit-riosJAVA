package br.ce.wcaquino.servicos;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
		
	}
	
	
	@Test
	public void testeLocacao() throws Exception {
		//cenario
		List<Filme> filmes = new ArrayList<>();
		Usuario usuario = new Usuario("Leonardo");
		Filme filme = new Filme("Titanic", 2, 5.0);
		filmes.add(filme);
		//acao
		
		Locacao locacao = service.alugarFilme(usuario, filmes);
			error.checkThat(locacao.getValor(), is(equalTo(5.0)));
			error.checkThat(DataUtils.isMesmaData(locacao.getDataLocacao(), new Date()), is(true));
			error.checkThat(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterDataComDiferencaDias(1)), CoreMatchers.is(true));
			
			
		
		
		//verificacao
	}

	
	@Test(expected = FilmesSemEstoqueException.class)
	public void testLocacao_filmeSemEstoque() throws Exception {
		
		//cenario
		List<Filme> filmes = new ArrayList<>();
		Usuario usuario = new Usuario("Leonardo");
		Filme filme = new Filme("Titanic", 0, 5.0);
		Filme filme1 = new Filme("Titanic", 0, 5.0);
		Filme filme2 = new Filme("Titanic", 0, 5.0);
		filmes.add(filme);
		filmes.add(filme1);
		filmes.add(filme2);
				
		//acao
				
		service.alugarFilme(usuario, filmes);
		
	}
	
	

	public void testLocacao_filmeSemEstoque2() {
		
		//cenario
		List<Filme> filmes = new ArrayList<>();
		Usuario usuario = new Usuario("Leonardo");
		Filme filme = new Filme("Titanic", 1, 5.0);
		Filme filme1 = new Filme("Titanic", 1, 5.0);
		Filme filme2 = new Filme("Titanic", 1, 5.0);
		filmes.add(filme);
		filmes.add(filme1);
		filmes.add(filme2);
				
		//acao
				
		try {
			service.alugarFilme(usuario, filmes);
			Assert.fail("Deveria lançar excecao");
		} catch (Exception e) {
			Assert.assertThat(e.getMessage(), CoreMatchers.is("Sem estoque"));
		}
		
	}
	
	
	@Test
	public void testLocacao_filmeSemEstoque3() throws Exception {
		
		//cenario
		
		List<Filme> filmes = new ArrayList<>();
		Usuario usuario = new Usuario("Leonardo");
		Filme filme = new Filme("Titanic", 0, 5.0);
		Filme filme1 = new Filme("Titanic", 0, 5.0);
		Filme filme2 = new Filme("Titanic", 0, 5.0);
		filmes.add(filme);
		filmes.add(filme1);
		filmes.add(filme2);
		
		exception.expect(FilmesSemEstoqueException.class);
		
		
		//acao
				
		service.alugarFilme(usuario, filmes);
		
		
		
	}
	
	@Test
	public void testLocacaoUsuarioVazio() throws FilmesSemEstoqueException {
		//cenario
		List<Filme> filmes = new ArrayList<>();
		Usuario usuario = new Usuario("Leonardo");
		Filme filme = new Filme("Titanic", 1, 5.0);
		Filme filme1 = new Filme("Titanic", 1, 5.0);
		Filme filme2 = new Filme("Titanic", 1, 5.0);
		filmes.add(filme);
		filmes.add(filme1);
		filmes.add(filme2);
		
		//acao
		try {
			service.alugarFilme(null, filmes);
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
	
	@Test
	public void devePagar75pctno3Filme() throws FilmesSemEstoqueException, LocadoraException {
		//cenario
		Usuario usuario = new Usuario("Leonardo");
		Filme filme = new Filme("Titanic", 1, 5.0);
		Filme filme1 = new Filme("Titanic", 1, 5.0);
		Filme filme2 = new Filme("Titanic", 1, 5.0);
		List<Filme> filmes = new ArrayList<>();
		filmes.add(filme);
		filmes.add(filme1);
		filmes.add(filme2);
		
		//acao
		
		Locacao locacao = service.alugarFilme(usuario, filmes);
		
		
		//verificar
		assertThat(locacao.getValor(), is(13.75));
		
		
	}
	
	@Test
	public void devePagar50pctno4Filme() throws FilmesSemEstoqueException, LocadoraException {
		//cenario
		Usuario usuario = new Usuario("Leonardo");
		Filme filme = new Filme("Titanic", 1, 4.0);
		Filme filme1 = new Filme("Titanic", 1, 4.0);
		Filme filme2 = new Filme("Titanic", 1, 4.0);
		Filme filme3 = new Filme("Titanic", 1, 4.0);
		List<Filme> filmes = new ArrayList<>();
		filmes.add(filme);
		filmes.add(filme1);
		filmes.add(filme2);
		filmes.add(filme3);
		
		//acao
		
		Locacao locacao = service.alugarFilme(usuario, filmes);
		
		
		//verificar
		assertThat(locacao.getValor(), is(13.00));
		
		
	}
	
	@Test
	public void devePagar25pctno5Filme() throws FilmesSemEstoqueException, LocadoraException {
		//cenario
		Usuario usuario = new Usuario("Leonardo");
		Filme filme = new Filme("Titanic", 1, 4.0);
		Filme filme1 = new Filme("Titanic", 1, 4.0);
		Filme filme2 = new Filme("Titanic", 1, 4.0);
		Filme filme3 = new Filme("Titanic", 1, 4.0);
		Filme filme4 = new Filme("Titanic", 1, 4.0);
		List<Filme> filmes = new ArrayList<>();
		filmes.add(filme);
		filmes.add(filme1);
		filmes.add(filme2);
		filmes.add(filme3);
		filmes.add(filme4);
		
		//acao
		
		Locacao locacao = service.alugarFilme(usuario, filmes);
		
		
		//verificar
		assertThat(locacao.getValor(), is(14.00));
		
		
	}
	
	@Test
	public void devePagar0pctno6Filme() throws FilmesSemEstoqueException, LocadoraException {
		//cenario
		Usuario usuario = new Usuario("Leonardo");
		Filme filme = new Filme("Titanic", 1, 4.0);
		Filme filme1 = new Filme("Titanic", 1, 4.0);
		Filme filme2 = new Filme("Titanic", 1, 4.0);
		Filme filme3 = new Filme("Titanic", 1, 4.0);
		Filme filme4 = new Filme("Titanic", 1, 4.0);
		Filme filme5 = new Filme("Titanic", 1, 4.0);
		List<Filme> filmes = new ArrayList<>();
		filmes.add(filme);
		filmes.add(filme1);
		filmes.add(filme2);
		filmes.add(filme3);
		filmes.add(filme4);
		filmes.add(filme5);
		
		//acao
		
		Locacao locacao = service.alugarFilme(usuario, filmes);
		
		
		//verificar
		assertThat(locacao.getValor(), is(14.00));
		
		
	}
	
}





















