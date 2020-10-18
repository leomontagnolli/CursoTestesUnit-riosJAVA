package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.utils.DataUtils.adicionarDias;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.ce.wcaquino.dao.LocacaoDAO;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exception.FilmesSemEstoqueException;
import br.ce.wcaquino.exception.LocadoraException;
import br.ce.wcaquino.utils.DataUtils;

public class LocacaoService {
	
	private LocacaoDAO dao;
	
	private SPCService spcService;
	
	private EmailService emailService;
	
	
	public Locacao alugarFilme(Usuario usuario, List<Filme> filme) throws FilmesSemEstoqueException, LocadoraException  {
		if(filme == null || filme.isEmpty()) {
			throw new LocadoraException("Filme vazio");
		}
		
		for(Filme f: filme) {
			if(f.getEstoque() == 0) {
				throw new FilmesSemEstoqueException("Sem estoque");
			}
		}
		
		if(usuario == null) {
			throw new LocadoraException("Usuario vazio");
		}
		
		boolean negativado;
		try {
			negativado = spcService.possuiNegativacao(usuario);
			
		} catch (Exception e) {
			throw new LocadoraException("Problemas com spc");
		
		}
		
		if(negativado) {
			throw new LocadoraException("Usuário negativado");
		}
		
		double valorTotal = 0;
		Locacao locacao = new Locacao();
		locacao.setFilme(filme);
		locacao.setUsuario(usuario);
		locacao.setDataLocacao(new Date());
		for(int i = 0; i< filme.size(); i++) {
			Filme filme1 = filme.get(i);
			Double valorFilme = filme1.getPrecoLocacao();
			switch (i) {
				case 2: valorFilme = valorFilme * 0.75; break;
				case 3: valorFilme = valorFilme* 0.5; break;
				case 4: valorFilme = valorFilme* 0.25; break;
				case 5: valorFilme = 0d; break;
			}
			valorTotal +=  valorFilme;
			
		}
		
		locacao.setValor(valorTotal);

		//Entrega no dia seguinte
		Date dataEntrega = new Date();
		dataEntrega = adicionarDias(dataEntrega, 1);
		if(DataUtils.verificarDiaSemana(dataEntrega, Calendar.SUNDAY)) {
			dataEntrega = adicionarDias(dataEntrega, 1);
		}
		locacao.setDataRetorno(dataEntrega);
	
		
		dao.salvar(locacao);
		
		return locacao;
	}
	
	public void notificarAtrasos() {
		List<Locacao> locacoes = dao.obterLocacoesPedentes();
		for (Locacao locacao: locacoes) {
			
			if(locacao.getDataRetorno().before(new Date()))
				emailService.notificarAtraso(locacao.getUsuario());
			
		}
	}
	
	public void prorrogarLocacao(Locacao locacao, int dias) {
		Locacao novaLocacao = new Locacao();
		novaLocacao.setUsuario(locacao.getUsuario());
		novaLocacao.setFilme(locacao.getFilme());
		novaLocacao.setDataLocacao(new Date());
		novaLocacao.setDataRetorno(DataUtils.obterDataComDiferencaDias(dias));
		novaLocacao.setValor(locacao.getValor() * dias);
		dao.salvar(novaLocacao);
	}


	
}