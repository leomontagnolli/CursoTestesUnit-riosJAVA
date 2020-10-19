package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.builder.UsuarioBuilder.umUsuario;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import br.ce.wcaquino.builder.FilmeBuilder;
import br.ce.wcaquino.builder.UsuarioBuilder;
import br.ce.wcaquino.dao.LocacaoDAO;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.matchers.MatchersPropios;
import br.ce.wcaquino.utils.DataUtils;

//@RunWith(PowerMockRunner.class)
//@PrepareForTest(LocacaoService.class)
public class LocacaoServiceTest_PoweMok {
	
	//@Rule
	public ErrorCollector error = new ErrorCollector();
	
	//@InjectMocks
	private LocacaoService service;
	
	//@Mock
	private SPCService spc;
	//@Mock
	private LocacaoDAO dao;
	//@Mock
	private EmailService mail;
	
	
	//@Rule
	public ExpectedException exception = ExpectedException.none();
	
	
	
	//@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		service = PowerMockito.spy(service);
	
		
	}
	
	
	//@Test
	public void testeLocacao() throws Exception {
		//PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(DataUtils.obterData(16, 10, 2020));
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, 16);
		calendar.set(Calendar.MONTH, Calendar.OCTOBER);
		calendar.set(Calendar.YEAR, 2020);
		PowerMockito.mockStatic(Calendar.class);
		PowerMockito.when(Calendar.getInstance()).thenReturn(calendar);
		
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


	
	//@Test
	public void naoDeveDevolverFilmeNoDomingo() throws Exception {	
		//cenario 
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilme().agora());
		
//		PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(DataUtils.obterData(17, 10, 2020));
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, 17);
		calendar.set(Calendar.MONTH, Calendar.OCTOBER);
		calendar.set(Calendar.YEAR, 2020);
		PowerMockito.mockStatic(Calendar.class);
		PowerMockito.when(Calendar.getInstance()).thenReturn(calendar);
		
		//acao
		Locacao retorno = service.alugarFilme(usuario, filmes);
		
		//verificacao
		assertThat(retorno.getDataRetorno(), MatchersPropios.caiEm(Calendar.MONDAY));
		//PowerMockito.verifyNew(Date.class, Mockito.times(2)).withNoArguments();
		
		PowerMockito.verifyStatic(Mockito.times(2));
		Calendar.getInstance();
		
	}
	

	
	//@Test
	public void deveAlugarFilmeSemCalcularValor() throws Exception {
		//cenario
		Usuario usuario = UsuarioBuilder.umUsuario().agora();
		List<Filme> filme = Arrays.asList(FilmeBuilder.umFilme().agora());
		
		
		PowerMockito.doReturn(1.0).when(service, "calcularValorLocacao", filme);
		
		//acao
		Locacao locacao = service.alugarFilme(usuario, filme);
		
		
		//verifi
		Assert.assertThat(locacao.getValor(), CoreMatchers.is(1.0));
		PowerMockito.verifyPrivate(service).invoke("calcularValorLocacao", filme);
		
	
		
	}
	
	//@Test
	public void deveCalcularValorLocacao() throws Exception {
		//cenario
		List<Filme> filme = Arrays.asList(FilmeBuilder.umFilme().agora());
		
		//acao
		Double valor = (Double) Whitebox.invokeMethod(service, "calcularValorLocacao", filme);
		
		
		//verifi
		Assert.assertThat(valor, is(5.0));
		
	}
}





















