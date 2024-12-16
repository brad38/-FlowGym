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
                        // ALUNO e RECEPCIONISTA GET
                        .requestMatchers(HttpMethod.GET,
                                "/aluno/recepcionista/cpf/{cpf}",
                                "/aluno/recepcionista/verificarVencimento/{cpf}")
                        .hasAnyRole("ALUNO", "RECEPCIONISTA")
                        // SOMENTE ALUNO GET
                        .requestMatchers(HttpMethod.GET,
                                "/aluno/exercicios/{matricula}") // aluno getByMatricula
                        .hasAnyRole("ALUNO")
                        // RECEPCIONISTA E INSTRUTOR GET
                        .requestMatchers(HttpMethod.GET,
                                "/instrutor/recepcionista/cpf/{cpf}") // recepcionista e instrutor getByCpf instrutor
                        .hasAnyRole("RECEPCIONISTA", "INSTRUTOR")
                        // ADMIN e RECEPCIONISTA endpoints GET
                        .requestMatchers(HttpMethod.GET,
                                "/aluno", //admin e recepcionista
                                "/admin/ids",
                                "/admin/ids/{id}",
                                "recepcionista/admin/ids/{id}",
                                "recepcionista/recepcionista/cpf/{cpf}", // admin e recepcionista
                                "/instrutor", //Recepcionista e admin
                                "instrutor/recepcionista/cpf/{cpf}") // recepcionista e admin
                        .hasAnyRole("ADMIN", "RECEPCIONISTA")
                        // Somente ADMIN GET
                        .requestMatchers(HttpMethod.GET,
                                "/aluno/admin/ids", //admin getAllId(),
                                "/aluno/admin/ids/{id}", //admin getById(),
                                "/instrutor/admin/ids", //admin listar os instrutores exibindo junto os ids
                                "/instrutor/admin/ids/{id}", //admin
                                "rececpcionista/admin/ids",
                                "/admin/ids/{id}") //admin
                        .hasAnyRole("ADMIN")
                        // SOMENTE INSTRUTOR GET
                        .requestMatchers(HttpMethod.GET,
                                "/fichas/instrutor/exercicios/{matricula}") // instrutor getByAlunoMatricula
                        .hasAnyRole("INSTRUTOR")
                        // ALUNO e RECEPCIONISTA POST
                        .requestMatchers(HttpMethod.POST,
                                "aluno/recepcionista/cadastro", //recepcionista save()
                                "aluno/recepcionista/usuariocadastro") //recepcionista e aluno cadastrarSenha()
                        .hasAnyRole("ALUNO", "RECEPCIONISTA")
                        // SOMENTE RECEPCIONISTA POST
                        .requestMatchers(HttpMethod.POST,
                                "/instrutor/recepcionista/cadastro", //recepcionista salva instrutor
                                "/instrutor/recepcionista/usuariocadastro") //recepcionista e instrutor
                        .hasAnyRole("RECEPCIONISTA")
                        // ADMIN E RECEPCIONISTA POST
                        .requestMatchers(HttpMethod.POST,
                                "/recepcionista/recepcionista/usuariocadastro") //admin e recepcionist
                        .hasAnyRole("ADMIN", "RECEPCIONISTA")
                        //SOMENTE ADMIN POST
                        .requestMatchers(HttpMethod.POST,
                                "/recepcionista/recepcionista/cadastro") //admin
                        .hasAnyRole("ADMIN")
                        // SOMENTE INSTRUTOR POST
                        .requestMatchers(HttpMethod.POST,
                                "/fichas/instrutor/criar") //instrutor saveficha()
                        .hasAnyRole("INSTRUTOR")
                        // SOMENTE ALUNO PUT
                        .requestMatchers(HttpMethod.PUT,
                                "/aluno/imprimir/{matricula}") // aluno
                        .hasAnyRole("ALUNO")
                        // SOMENTE RECEPCIONISTA PUT
                        .requestMatchers(HttpMethod.PUT,
                                "/aluno/recepcionista/atualizar/cpf/{cpf}", //recepcionista updateByCpf()
                                "/aluno/recepcionista/zerarVencimento/{cpf}", // recepcionista zerarVencimento()
                                "/apagar/{id}", //admin deleteById()
                                "/instrutor/recepcionista/atualizar/cpf/{cpf}") //recepcionista atualiza instrutor
                        .hasAnyRole("RECEPCIONISTA")
                        // SOMENTE ADMIN PUT
                        .requestMatchers(HttpMethod.PUT,
                                "/recepcionista/atualizar/{id}", //admin updateById()
                                "/instrutor/atualizar/{id}", //admin
                                "/recepcionista/atualizar/{id}", //admin
                                "/recepcionista/recepcionista/atualizar/cpf/{cpf}", //admin
                                "/recepcionista/recepcionista/atualizar/cpf/{cpf}") //admin
                        .hasAnyRole("ADMIN")
                        // SOMENTE INSTRUTOR PUT
                        .requestMatchers(HttpMethod.PUT,
                                "fichas/instrutor/atualizar/{matricula}") //instrutor atualizarExercicios()
                        .hasAnyRole("INSTRUTOR")

                        // INSTRUTOR e ADMIN DELETE
                        .requestMatchers(HttpMethod.DELETE, "/fichas/instrutor/{matricula}") // instrutor deletar ficha de treino
                        .hasAnyRole("ADMIN", "INSTRUTOR")
                        // SOMENTE ADMIN DELETE
                        .requestMatchers(HttpMethod.DELETE,
                                "/aluno/apagar/{id}", //admin
                                "/instrutor/apagar/{id}", //admin
                                "instrutor/apagar/cpf/{cpf}", //admin
                                "/recepcionista/apagar/cpf/{cpf}")
                        .hasAnyRole("ADMIN")//admin

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
    public static void main(String[] args) {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        String Senha = JOptionPane.showInputDialog(null, "Digite a senha a ser criptografada do admin");
        String senhaCriptografada = encoder.encode(Senha);
        JOptionPane.showInputDialog(null, "Senha criptografada:", senhaCriptografada);
        System.out.println("\nSenha criptografada: " + senhaCriptografada + "\n");
    }
}

