package br.com.corretor.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.br.CPF;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CorretorDTO {
    private Long id;
    
    @NotNull(message = "ID da empresa é obrigatório")
    private Long empresaId;
    
    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 255, message = "Nome deve ter entre 3 e 255 caracteres")
    private String nome;
    
    @NotBlank(message = "CPF é obrigatório")
    @CPF(message = "CPF inválido")
    private String cpf;
    
    private String rg;
    
    @Past(message = "Data de nascimento deve ser no passado")
    private LocalDate dataNascimento;
    
    @NotBlank(message = "Endereço é obrigatório")
    private String endereco;
    
    private String bairro;
    
    @NotBlank(message = "Cidade é obrigatória")
    private String cidade;
    
    @NotBlank(message = "Estado é obrigatório")
    @Size(min = 2, max = 2, message = "Estado deve ter 2 caracteres")
    private String estado;
    
    @Pattern(regexp = "\\d{8}", message = "CEP deve conter 8 dígitos")
    private String cep;
    
    private String telefone;
    
    @Pattern(regexp = "\\d{10,11}", message = "Celular deve conter 10 ou 11 dígitos")
    private String celular;
    
    @Email(message = "Email inválido")
    private String email;
    
    @Pattern(regexp = "\\d{10,11}", message = "WhatsApp deve conter 10 ou 11 dígitos")
    private String whatsapp;
    
    private LocalDate dataCadastro;
    
    @DecimalMin(value = "0.0", message = "Comissão padrão deve ser maior ou igual a 0")
    @DecimalMax(value = "100.0", message = "Comissão padrão deve ser menor ou igual a 100")
    private BigDecimal comissaoPadrao;
    
    private String observacoes;
    
    private Boolean ativo;
    
    private Boolean bloqueado;
    
    private String motivoBloqueio;
    
    @NotBlank(message = "Número de registro é obrigatório")
    private String numeroRegistro;
    
    @NotNull(message = "Data de registro é obrigatória")
    @Past(message = "Data de registro deve ser no passado")
    private LocalDate dataRegistro;
    
    @Future(message = "Data de validade do registro deve ser no futuro")
    private LocalDate dataValidadeRegistro;
    
    @NotBlank(message = "Tipo de registro é obrigatório")
    private String tipoRegistro;
    
    @NotBlank(message = "Órgão de registro é obrigatório")
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

