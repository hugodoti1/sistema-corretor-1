package br.com.corretor.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("corretores")
public class Corretor {
    @Id
    private Long id;
    
    @Column("empresa_id")
    private Long empresaId;
    
    private String nome;
    
    private String cpf;
    
    private String rg;
    
    @Column("data_nascimento")
    private LocalDate dataNascimento;
    
    private String endereco;
    
    private String bairro;
    
    private String cidade;
    
    private String estado;
    
    private String cep;
    
    private String telefone;
    
    private String celular;
    
    private String email;
    
    private String whatsapp;
    
    @Column("data_cadastro")
    private LocalDate dataCadastro;
    
    @Column("comissao_padrao")
    private BigDecimal comissaoPadrao;
    
    private String observacoes;
    
    private Boolean ativo;
    
    private Boolean bloqueado;
    
    @Column("motivo_bloqueio")
    private String motivoBloqueio;
    
    @Column("numero_registro")
    private String numeroRegistro;
    
    @Column("data_registro")
    private LocalDate dataRegistro;
    
    @Column("data_validade_registro")
    private LocalDate dataValidadeRegistro;
    
    @Column("tipo_registro")
    private String tipoRegistro;
    
    @Column("orgao_registro")
    private String orgaoRegistro;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getEmpresaId() {
		return empresaId;
	}

	public void setEmpresaId(Long empresaId) {
		this.empresaId = empresaId;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public String getRg() {
		return rg;
	}

	public void setRg(String rg) {
		this.rg = rg;
	}

	public LocalDate getDataNascimento() {
		return dataNascimento;
	}

	public void setDataNascimento(LocalDate dataNascimento) {
		this.dataNascimento = dataNascimento;
	}

	public String getEndereco() {
		return endereco;
	}

	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}

	public String getBairro() {
		return bairro;
	}

	public void setBairro(String bairro) {
		this.bairro = bairro;
	}

	public String getCidade() {
		return cidade;
	}

	public void setCidade(String cidade) {
		this.cidade = cidade;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public String getCep() {
		return cep;
	}

	public void setCep(String cep) {
		this.cep = cep;
	}

	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	public String getCelular() {
		return celular;
	}

	public void setCelular(String celular) {
		this.celular = celular;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getWhatsapp() {
		return whatsapp;
	}

	public void setWhatsapp(String whatsapp) {
		this.whatsapp = whatsapp;
	}

	public LocalDate getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(LocalDate dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public BigDecimal getComissaoPadrao() {
		return comissaoPadrao;
	}

	public void setComissaoPadrao(BigDecimal comissaoPadrao) {
		this.comissaoPadrao = comissaoPadrao;
	}

	public String getObservacoes() {
		return observacoes;
	}

	public void setObservacoes(String observacoes) {
		this.observacoes = observacoes;
	}

	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	public Boolean getBloqueado() {
		return bloqueado;
	}

	public void setBloqueado(Boolean bloqueado) {
		this.bloqueado = bloqueado;
	}

	public String getMotivoBloqueio() {
		return motivoBloqueio;
	}

	public void setMotivoBloqueio(String motivoBloqueio) {
		this.motivoBloqueio = motivoBloqueio;
	}

	public String getNumeroRegistro() {
		return numeroRegistro;
	}

	public void setNumeroRegistro(String numeroRegistro) {
		this.numeroRegistro = numeroRegistro;
	}

	public LocalDate getDataRegistro() {
		return dataRegistro;
	}

	public void setDataRegistro(LocalDate dataRegistro) {
		this.dataRegistro = dataRegistro;
	}

	public LocalDate getDataValidadeRegistro() {
		return dataValidadeRegistro;
	}

	public void setDataValidadeRegistro(LocalDate dataValidadeRegistro) {
		this.dataValidadeRegistro = dataValidadeRegistro;
	}

	public String getTipoRegistro() {
		return tipoRegistro;
	}

	public void setTipoRegistro(String tipoRegistro) {
		this.tipoRegistro = tipoRegistro;
	}

	public String getOrgaoRegistro() {
		return orgaoRegistro;
	}

	public void setOrgaoRegistro(String orgaoRegistro) {
		this.orgaoRegistro = orgaoRegistro;
	}
    
    
    
    
}

