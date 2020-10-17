package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.builder.UsuarioBuilder.umUsuario;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import br.ce.wcaquino.builder.FilmeBuilder;
import br.ce.wcaquino.builder.UsuarioBuilder;
import br.ce.wcaquino.dao.LocacaoDAO;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exception.FilmesSemEstoqueException;
import br.ce.wcaquino.exception.LocadoraException;
import br.ce.wcaquino.matchers.MatchersPropios;
import br.ce.wcaquino.utils.DataUtils;

public class LocacaoServiceTest {
	
	@Rule
	public ErrorCollector error = new ErrorCollector();
	
	private LocacaoService service;
	private SPCService spc;
	private LocacaoDAO dao;
	
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	
	
	@Before
	public void setup() {
		service = new LocacaoService();
		dao = Mockito.mock(LocacaoDAO.class);
		service.setLocacaoDAO(dao);
		spc = Mockito.mock(SPCService.class);
		service.setSpcService(spc);
		
	}
	
	
	@Test
	public void testeLocacao() throws Exception {
		Assume.assumeFalse(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
		
		//cenario
		List<Filme> filmes = new ArrayList<>();
		Usuario usuario = umUsuario().agora();
		Filme filme = FilmeBuilder.umFilme().agora();
		filmes.add(filme);
		//acao
		
		Locacao locacao = service.alugarFilme(usuario, filmes);
			error.checkThat(locacao.getValor(), is(equalTo(5.0)));
			error.checkThat(locacao.getDataLocacao(), MatchersPropios.ehHoje());
			error.checkThat(locacao.getDataRetorno(), MatchersPropios.ehHojeComDiferencaDias(1));
			
		
		
		
		//verificacao
	}

	
	@Test(expected = FilmesSemEstoqueException.class)
	public void testLocacao_filmeSemEstoque() throws Exception {
		
		//cenario
		List<Filme> filmes = new ArrayList<>();
		Usuario usuario = umUsuario().agora();
		Filme filme = FilmeBuilder.umFilmeSemEstoque().agora();
		Filme filme1 = FilmeBuilder.umFilmeSemEstoque().agora();
		Filme filme2 = FilmeBuilder.umFilmeSemEstoque().agora();
		filmes.add(filme);
		filmes.add(filme1);
		filmes.add(filme2);
				
		//acao
				
		service.alugarFilme(usuario, filmes);
		
	}
	
	
	@Test
	public void testLocacao_filmeSemEstoque2() {
		
		//cenario
		List<Filme> filmes = new ArrayList<>();
		Usuario usuario = umUsuario().agora();
		Filme filme = FilmeBuilder.umFilmeSemEstoque().agora();
		Filme filme1 = FilmeBuilder.umFilmeSemEstoque().agora();
		Filme filme2 = FilmeBuilder.umFilmeSemEstoque().agora();
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
		Usuario usuario = umUsuario().agora();
		Filme filme = FilmeBuilder.umFilme().semEstoque().agora();
		Filme filme1 = FilmeBuilder.umFilme().semEstoque().agora();
		Filme filme2 = FilmeBuilder.umFilme().semEstoque().agora();
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
		Filme filme = FilmeBuilder.umFilme().agora();
		Filme filme1 =FilmeBuilder.umFilme().agora();
		Filme filme2 = FilmeBuilder.umFilme().agora();
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
		Usuario usuario = umUsuario().agora();
		
		exception.expect(LocadoraException.class);
		
		
		//acao
	
		service.alugarFilme(usuario, null);

		
		
	}
	
	@Test
	public void naoDeveDevolverFilmeNoDomingo() throws FilmesSemEstoqueException, LocadoraException {
		Assume.assumeTrue(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
		
		//cenario 
		Usuario usuario = umUsuario().agora();
		Filme filme = FilmeBuilder.umFilme().agora();
		List<Filme> filmes = new ArrayList<>();
		filmes.add(filme);
		
		//acao
		Locacao retorno = service.alugarFilme(usuario, filmes);
		
		//verificacao
		assertThat(retorno.getDataRetorno(), MatchersPropios.caiEm(Calendar.MONDAY));
		
	}
	
	@Test
	public void naoDeveAlugarFilmeParaNegativado() throws FilmesSemEstoqueException, LocadoraException {
		//cenario
		Usuario usuario = UsuarioBuilder.umUsuario().agora();
		Usuario usuario2 = UsuarioBuilder.umUsuario().agora();
		List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilme().agora());
		
		Mockito.when(spc.possuiNegativacao(usuario)).thenReturn(true);
		
		exception.expect(LocadoraException.class);
		exception.expectMessage("Usuário negativado");
		
		//acao
		service.alugarFilme(usuario2, filmes);
		
		
	}
	
	
}





















