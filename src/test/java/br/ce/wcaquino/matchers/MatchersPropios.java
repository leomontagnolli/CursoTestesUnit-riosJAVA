package br.ce.wcaquino.matchers;

public class MatchersPropios {
	
	public static DiasSemanasMatchers caiEm(Integer diaSemana) {
		return new DiasSemanasMatchers(diaSemana);
	}
	
	public static DataDiferenciaDiasMatcher ehHojeComDiferencaDias(Integer qtdDias) {
		return new DataDiferenciaDiasMatcher(qtdDias);
	}
	
	public static DataDiferenciaDiasMatcher ehHoje() {
		return new DataDiferenciaDiasMatcher(0);
	}

}
