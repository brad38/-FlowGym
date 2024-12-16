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

                .requestMatchers(HttpMethod.GET, "/api/current-user").authenticated()
                    // Endpoints GET
                    .requestMatchers(HttpMethod.GET,
                        "/aluno",
                        "/aluno/admin/ids",
                        "/aluno/admin/ids/{id}",
                        "/aluno/recepcionista/cpf/{cpf}",
                        "/fichas/instrutor/exercicios/{matricula}",
                        "/fichas/aluno/exercicios/{matricula}",
                        "/recepcionista/admin/ids/{id}",
                        "/recepcionista/recepcionista/cpf/cpf{}",
                        "/instrutor/admin/ids/{id}",
                        "/instrutor/recepcionista/cpf/{cpf}")
                    .hasAnyRole("ADMIN","ALUNO","RECEPCIONISTA","INSTRUTOR")
                    // Endpoints POST
                    .requestMatchers(HttpMethod.POST,
                            "aluno/recepcionista/cadastro",
                            "aluno/recepcionista/usuariocadastro",
                            "fichas/instrutor/criar",
                            "recepcionista/recepcionista/usuariocadastro",
                            "instrutor/recepcionista/cadastro",
                            "instrutor/recepcionista/usuariocadastro")
                    .hasAnyRole("ADMIN","ALUNO","RECEPCIONISTA","INSTRUTOR")
                    // Endpoints PUT
                    .requestMatchers(HttpMethod.PUT,
                            "aluno/atualizar/{id}",
                            "aluno/recepcionista/atualizar/cpf/{cpf}",
                            "aluno/recepcionista/zerarVencimento/{cpf}",
                            "fichas/instrutor/atualizar/{matricula}",
                            "fichas/imprimir/{matricula}",
                            "recepcionista/atualizar/{id}",
                            "recepcionista/recepcionista/atualizar/cpf/{cpf}",
                            "instrutor/atualizar/{id}",
                            "instrutor/recepcionista/atualizar/cpf/{cpf}")
                    .hasAnyRole("ADMIN","ALUNO","RECEPCIONISTA","INSTRUTOR")
                    // Endpoints DELETE
                    .requestMatchers(HttpMethod.DELETE,
                            "aluno/apagar/{id}",
                            "fichas/instrutor/{matricula}",
                            "recepcionista/apagar/{id}",
                            "recepcionista/apagar/cpf/{cpf}",
                            "instrutor/apagar/{id}",
                            "instrutor/apagar/cpf/{cpf}")
                    .hasAnyRole("ADMIN","ALUNO","RECEPCIONISTA","INSTRUTOR")
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

