package com.flowgym.crud.security;

import javax.sql.DataSource;
import javax.swing.JOptionPane;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth // Isso configura as permissões de acesso para os endpoints.
            .requestMatchers(
                "/aluno/recepcionista/**",
                "/aluno", 
                "/recepcionista/recepcionista/usuariocadastro", 
                "/recepcionista/recepcionista/cpf/{cpf}",
                "/recepcionista/recepcionista/atualizar/cpf/{cpf}",
                "/instrutor/recepcionista/cpf/{cpf}",
                "/instrutor/recepcionista/cadastro",
                "/instrutor/recepcionista/usuariocadastro",
                "/instrutor/recepcionista/atualizar/cpf/{cpf}").hasAnyRole("RECEPCIONISTA", "ADMIN")
                .requestMatchers(
                "/fichas/imprimir/{matricula}", 
                "/aluno/recepcionista/cpf/{cpf}", 
                "/aluno/recepcionista/usuariocadastro", 
                "/fichas/aluno/exercicios/{matricula}").hasAnyRole("ALUNO", "ADMIN")
                .requestMatchers(
                "/aluno",
                "/fichas/instrutor/**", 
                "/instrutor/recepcionista/cpf/{cpf}",
                "/instrutor/recepcionista/usuariocadastro").hasAnyRole("INSTRUTOR", "ADMIN")
                .anyRequest().hasRole("ADMIN") // Permite ADMIN acessar todos os endpoint que não estão mapeados a outros papeis
            )
            .formLogin(form -> form.permitAll()) //Permite que qualquer um acesse a tela de login
            .httpBasic(Customizer.withDefaults()); //Ativa a autenticação HTTP Basic (nome de usuário e senha no cabeçalho da requisição)
        return http.build();
    }

    @Bean //O "dataSource" é injetado automaticamente e fornece uma conexão com o banco de dados.
    public UserDetailsService userDetailsService(DataSource dataSource) { // Isso carrega as informações de um usuário, como o nome de usuário, senha, roles (permissões) e se o usuário está ativo ou não 
        JdbcUserDetailsManager userDetailsManager = new JdbcUserDetailsManager(dataSource); // Neste caso, está sendo usado o JdbcUserDetailsManager, que obtém essas informações a partir de um banco de dados
        userDetailsManager.setUsersByUsernameQuery("SELECT username, password, enabled FROM public.usuario WHERE username = ?"); // Define a consulta SQL que será executada para recuperar as informações do usuário
        userDetailsManager.setAuthoritiesByUsernameQuery("SELECT username, role FROM public.usuario WHERE username = ?"); // Define a consulta SQL que será executada para recuperar as permissões (roles) do usuário. No caso, ele está buscando a coluna "role" para definir o que o usuário pode fazer (por exemplo, ADMIN, RECEPCIONISTA).
        return userDetailsManager; // Retorna o "JdbcUserDetailsManager" configurado, que será usado pelo Spring Security para autenticar e autorizar os usuários
    }

    // "PasswordEnconder" é utilizado para codificar (ou criptografar) senhas de maneira segura.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Esse codificador gera um hash criptografado da senha usando um algoritmo avançado
    }

    //Contexto estático pra criptografar uma senha
    public static void main(String[] args){
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        String Senha =  JOptionPane.showInputDialog(null, "Digite a senha a ser criptografada do admin");
        String senhaCriptografada = encoder.encode(Senha);
        JOptionPane.showInputDialog(null, "Senha criptografada:",senhaCriptografada);
        System.out.println("\nSenha criptografada: " + senhaCriptografada + "\n");
    }
}

