package br.com.corretor.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FuncionarioDTO {
    private Long id;
    private Long empresaId;

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 255, message = "Nome deve ter entre 3 e 255 caracteres")
    private String nome;

    @NotBlank(message = "CPF é obrigatório")
    @Pattern(regexp = "\\d{11}", message = "CPF deve conter 11 dígitos")
    private String cpf;

    private String rg;

    @Past(message = "Data de nascimento deve ser no passado")
    private LocalDate dataNascimento;

    private String endereco;

    @Pattern(regexp = "^\\+?[1-9][0-9]{7,14}$", message = "Telefone inválido")
    private String telefone;

    @Email(message = "Email deve ser válido")
    private String email;

    @Pattern(regexp = "^\\+?[1-9][0-9]{7,14}$", message = "WhatsApp inválido")
    private String whatsapp;

    private String pix;
    private String carteiraTrabalho;

    @NotNull(message = "Data de admissão é obrigatória")
    private LocalDate dataAdmissao;

    private LocalDate dataDemissao;

    @DecimalMin(value = "0.0", message = "Comissão não pode ser negativa")
    @DecimalMax(value = "100.0", message = "Comissão não pode ser maior que 100%")
    private BigDecimal comissaoVendas;

    @DecimalMin(value = "0.0", message = "Bonificação não pode ser negativa")
    private BigDecimal valorBonificacao;

    private Boolean ativo;

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

	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
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

	public String getPix() {
		return pix;
	}

	public void setPix(String pix) {
		this.pix = pix;
	}

	public String getCarteiraTrabalho() {
		return carteiraTrabalho;
	}

	public void setCarteiraTrabalho(String carteiraTrabalho) {
		this.carteiraTrabalho = carteiraTrabalho;
	}

	public LocalDate getDataAdmissao() {
		return dataAdmissao;
	}

	public void setDataAdmissao(LocalDate dataAdmissao) {
		this.dataAdmissao = dataAdmissao;
	}

	public LocalDate getDataDemissao() {
		return dataDemissao;
	}

	public void setDataDemissao(LocalDate dataDemissao) {
		this.dataDemissao = dataDemissao;
	}

	public BigDecimal getComissaoVendas() {
		return comissaoVendas;
	}

	public void setComissaoVendas(BigDecimal comissaoVendas) {
		this.comissaoVendas = comissaoVendas;
	}

	public BigDecimal getValorBonificacao() {
		return valorBonificacao;
	}

	public void setValorBonificacao(BigDecimal valorBonificacao) {
		this.valorBonificacao = valorBonificacao;
	}

	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}
    
    
}
