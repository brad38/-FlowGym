package com.flowgym.crud.security;

import javax.sql.DataSource;
import javax.swing.JOptionPane;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
        .csrf(csrf -> csrf.disable()) // Desabilita CSRF (necessário para APIs REST)
        .cors(cors -> cors.configure(http)) // Configuração CORS
        .authorizeHttpRequests(auth -> auth
        
            // Aluno: Endpoints GET
            .requestMatchers(HttpMethod.GET, "/aluno/recepcionista/cpf/{cpf}").hasAnyRole("ALUNO", "ADMIN", "RECEPCIONISTA")
            .requestMatchers(HttpMethod.GET, "/fichas/aluno/exercicios/{matricula}").hasAnyRole("ALUNO", "ADMIN", "INSTRUTOR") 
            .requestMatchers(HttpMethod.GET, "/aluno/recepcionista/verificarVencimento/{cpf}").hasAnyRole("ALUNO", "ADMIN") 
        
            // Aluno: Endpoints PUT
            .requestMatchers(HttpMethod.PUT, "/fichas/imprimir/{matricula}").hasAnyRole("ALUNO", "ADMIN")
        
            // Aluno: Endpoints POST
            .requestMatchers(HttpMethod.POST, "/aluno/recepcionista/usuariocadastro").permitAll()
        
            // Recepcionista: Endpoints GET
            .requestMatchers(HttpMethod.GET, "/aluno").hasAnyRole("RECEPCIONISTA", "ADMIN")
            .requestMatchers(HttpMethod.GET, "/recepcionista/recepcionista/cpf/{cpf}").hasAnyRole("RECEPCIONISTA", "ADMIN")
            .requestMatchers(HttpMethod.GET, "/instrutor/recepcionista/cpf/{cpf}").hasAnyRole("RECEPCIONISTA", "ADMIN", "INSTRUTOR")
        
            // Recepcionista: Endpoints POST
            .requestMatchers(HttpMethod.POST, "/aluno/recepcionista/**").hasAnyRole("RECEPCIONISTA", "ADMIN")
            .requestMatchers(HttpMethod.POST, "/recepcionista/recepcionista/usuariocadastro").permitAll()
            .requestMatchers(HttpMethod.POST, "/instrutor/recepcionista/cadastro").hasAnyRole("RECEPCIONISTA", "ADMIN")
            .requestMatchers(HttpMethod.POST, "/instrutor/recepcionista/usuariocadastro").permitAll()
        
            // Recepcionista: Endpoints PUT
            .requestMatchers(HttpMethod.PUT, "/aluno/recepcionista/zerarVencimento/{cpf}").hasAnyRole("RECEPCIONISTA", "ADMIN")
            .requestMatchers(HttpMethod.PUT, "/instrutor/recepcionista/atualizar/cpf/{cpf}").hasAnyRole("RECEPCIONISTA", "ADMIN")
        
            // Instrutor: Endpoints GET
            .requestMatchers(HttpMethod.GET, "/fichas/instrutor/exercicios/{matricula}").hasAnyRole("INSTRUTOR", "ADMIN")
        
            // Instrutor: Endpoints POST
            .requestMatchers(HttpMethod.POST, "/fichas/instrutor/**").hasAnyRole("INSTRUTOR", "ADMIN")
        
            // Instrutor: Endpoints PUT
            .requestMatchers(HttpMethod.PUT, "/fichas/instrutor/atualizar/{matricula}").hasAnyRole("INSTRUTOR", "ADMIN")
        
            // Instrutor: Endpoints DELETE
            .requestMatchers(HttpMethod.DELETE, "/fichas/instrutor/{matricula}").hasAnyRole("INSTRUTOR", "ADMIN")
        
            // Admin: Endpoints GET
            .requestMatchers(HttpMethod.GET, "/instrutor/admin/ids").hasRole("ADMIN")
            .requestMatchers(HttpMethod.GET, "/instrutor/admin/ids/{id}").hasRole("ADMIN")
            .requestMatchers(HttpMethod.GET, "/aluno/admin/ids").hasRole("ADMIN")
            .requestMatchers(HttpMethod.GET, "/aluno/admin/ids/{id}").hasRole("ADMIN")

            // Admin: Endpoints PUT
            .requestMatchers(HttpMethod.PUT, "/recepcionista/atualizar/{id}").hasRole("ADMIN")
            .requestMatchers(HttpMethod.PUT, "/recepcionista/recepcionista/atualizar/cpf/{cpf}").hasRole("ADMIN")
            .requestMatchers(HttpMethod.PUT, "/instrutor/atualizar/{id}").hasRole("ADMIN")
            .requestMatchers(HttpMethod.PUT, "/aluno/atualizar/{id}").hasRole("ADMIN")

            // Admin: Endpoints DELETE
            .requestMatchers(HttpMethod.DELETE, "/recepcionista/apagar/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.DELETE, "/instrutor/apagar/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.DELETE, "/aluno/apagar/{id}").hasRole("ADMIN")
        
            // Permitir acesso público ao endpoint /current-user (para retornar as informações do usuário autenticado)
            .requestMatchers(HttpMethod.GET, "/api/current-user").authenticated() 
        
            // Garante que qualquer outro endpoint seja acessado apenas por usuários com a role ADMIN
            .anyRequest().hasRole("ADMIN") 
        )
        .formLogin(form -> form.permitAll()) // Permite que qualquer um acesse a tela de login
        .httpBasic(Customizer.withDefaults()); // Habilita autenticação HTTP Basic (nome de usuário e senha no cabeçalho da requisição)
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

    //Bean responsável por permitir requisições de diferentes origens
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://127.0.0.1:5500")
                        .allowedMethods("GET", "POST", "PUT", "DELETE");
            }
        };
    }

    //Contexto estático pra criptografar uma senha
    public static void main(String[] args){
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        String Senha = JOptionPane.showInputDialog(null, "Digite a senha a ser criptografada do admin");
        String senhaCriptografada = encoder.encode(Senha);
        JOptionPane.showInputDialog(null, "Senha criptografada:", senhaCriptografada);
        System.out.println("\nSenha criptografada: " + senhaCriptografada + "\n");
    }
}

