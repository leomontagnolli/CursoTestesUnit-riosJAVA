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
		
		if(spcService.possuiNegativacao(usuario)) {
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
	
	public void setLocacaoDAO(LocacaoDAO dao) {
		this.dao = dao;
		
	}
	
	public void setSpcService(SPCService spc) {
		this.spcService = spc;
		
		
	}

	
}