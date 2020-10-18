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
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import br.ce.wcaquino.builder.FilmeBuilder;
import br.ce.wcaquino.builder.LocacaoBuilder;
import br.ce.wcaquino.builder.UsuarioBuilder;
import br.ce.wcaquino.dao.LocacaoDAO;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exception.FilmesSemEstoqueException;
import br.ce.wcaquino.exception.LocadoraException;
import br.ce.wcaquino.matchers.MatchersPropios;
import br.ce.wcaquino.utils.DataUtils;

@RunWith(PowerMockRunner.class)
@PrepareForTest(LocacaoService.class)
public class LocacaoServiceTest {
	
	@Rule
	public ErrorCollector error = new ErrorCollector();
	
	@InjectMocks
	private LocacaoService service;
	
	@Mock
	private SPCService spc;
	@Mock
	private LocacaoDAO dao;
	@Mock
	private EmailService mail;
	
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	
		
	}
	
	
	@Test
	public void testeLocacao() throws Exception {
		PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(DataUtils.obterData(16, 10, 2020));
		
		//cenario
		List<Filme> filmes = new ArrayList<>();
		Usuario usuario = umUsuario().agora();
		Filme filme = FilmeBuilder.umFilme().agora();
		filmes.add(filme);
		//acao
		
		Locacao locacao = service.alugarFilme(usuario, filmes);
			error.checkThat(locacao.getValor(), is(equalTo(5.0)));
			error.checkThat(DataUtils.isMesmaData(locacao.getDataLocacao(), DataUtils.obterData(16, 10, 2020)), CoreMatchers.is(true));
			error.checkThat(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterData(17, 10, 2020)), CoreMatchers.is(true));
			
		
		
		
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
	public void naoDeveDevolverFilmeNoDomingo() throws Exception {	
		//cenario 
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilme().agora());
		
		PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(DataUtils.obterData(17, 10, 2020));
		
		//acao
		Locacao retorno = service.alugarFilme(usuario, filmes);
		
		//verificacao
		assertThat(retorno.getDataRetorno(), MatchersPropios.caiEm(Calendar.MONDAY));
		
	}
	
	@Test
	public void naoDeveAlugarFilmeParaNegativado() throws Exception {
		//cenario
		Usuario usuario = UsuarioBuilder.umUsuario().agora();
		List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilme().agora());
		
		Mockito.when(spc.possuiNegativacao(usuario)).thenReturn(true);
		
		exception.expect(LocadoraException.class);
		exception.expectMessage("Usuário negativado");
		
		//acao
		service.alugarFilme(usuario, filmes);
		
		//verify
		
		Mockito.verify(spc).possuiNegativacao(usuario);
		
		
	}
	
	@Test
	public void deveEnviarEmailParaLocacoesAtrasdas() {
		//cenario
		Usuario usuario = umUsuario().agora();
		Usuario usuario2 = new Usuario("LEO");
		List<Locacao> locacoes = 
				Arrays.asList(
						LocacaoBuilder.umLocacao()
						.comUsuario(usuario)
						.comDataRetorno(DataUtils
								.obterDataComDiferencaDias(-2))
								.agora(),
						LocacaoBuilder.umLocacao().comUsuario(usuario2).comDataRetorno(
								DataUtils.obterDataComDiferencaDias(2)).agora());
		
		Mockito.when(dao.obterLocacoesPedentes()).thenReturn(locacoes);
			
		
		//acao
		service.notificarAtrasos();
		
		
		//verificacao
		Mockito.verify(mail, Mockito.times(1)).notificarAtraso(Mockito.any(Usuario.class));
		Mockito.verify(mail).notificarAtraso(usuario);
		Mockito.verify(mail, Mockito.never()).notificarAtraso(usuario2);
		Mockito.verifyNoMoreInteractions(mail);
	}
	
	
	@Test
	public void deveTratarErroNoSpc() throws Exception {
		//cenario
		Usuario usuario = UsuarioBuilder.umUsuario().agora();
		List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilme().agora());
		
		Mockito.when(spc.possuiNegativacao(usuario)).thenThrow(new Exception("Falha catastrófica"));
		
		//verificacao
		exception.expect(LocadoraException.class);
		exception.expectMessage("Problemas com spc");
		
		//acao
		service.alugarFilme(usuario, filmes);
		
		
		//verificacao
		
		
	}
	
	
	@Test
	public void deveProrrogarUmaLocacao() {
		//cenario
		Locacao locacao = LocacaoBuilder.umLocacao().agora();
		
		//acao
		service.prorrogarLocacao(locacao, 3);
		
		//verificacao
		ArgumentCaptor<Locacao> argCapt = ArgumentCaptor.forClass(Locacao.class);
		Mockito.verify(dao).salvar(argCapt.capture());
		Locacao locacaoRetornada = argCapt.getValue();
		
		error.checkThat(locacaoRetornada.getValor(), CoreMatchers.is(12.0));
		error.checkThat(locacaoRetornada.getDataLocacao(), MatchersPropios.ehHoje());
		error.checkThat(locacaoRetornada.getDataRetorno(), MatchersPropios.ehHojeComDiferencaDias(3));
	}
}





















