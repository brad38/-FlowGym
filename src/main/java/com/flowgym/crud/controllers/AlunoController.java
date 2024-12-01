package com.flowgym.crud.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flowgym.crud.dtos.AlunoDto;
import com.flowgym.crud.dtos.AlunoLoginDto;
import com.flowgym.crud.model.AlunoLoginModel;
import com.flowgym.crud.model.AlunoModel;
import com.flowgym.crud.repositories.AlunoRepository;
import com.flowgym.crud.repositories.LoginRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/aluno")
public class AlunoController {

    @Autowired
    AlunoRepository aRepository;

    @Autowired
    private LoginRepository alRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserDetailsService userDetailsService;  // Usado para criar usuário no Spring Security

    //Metodo pra gerar uma matricula
    private String gerarMatricula(){
        String maiorMatricula = aRepository.findMaxMatricula();
        int novaMatricula;
        if (maiorMatricula == null){
            novaMatricula = 1000;
        } else {
            novaMatricula = Integer.parseInt(maiorMatricula) + 1;
        }

        if (novaMatricula > 9999){
            throw new IllegalStateException("O numero máximo de matriculas foi antigido");
    }
    return String.valueOf(novaMatricula);
    }

    //Esse metodo pega todos os alunos do banco de dados e exibi, mas sem os Id
    @GetMapping //Recepcionista e instrutor
    public ResponseEntity getAll(){
        List<AlunoModel> listarAlunos = aRepository.findAll();
        // Cria uma lista de AlunoDto a partir da lista de AlunoModel
        List<AlunoDto> listaAlunoDto = new ArrayList<>();
        for (AlunoModel alunoModel : listarAlunos) {
        AlunoDto alunoDto = new AlunoDto(
            alunoModel.getNome(),
            alunoModel.getNascimento(),
            alunoModel.getEmail(),
            alunoModel.getTelefone(),
            alunoModel.getCpf(),
            alunoModel.isMenor(),
            alunoModel.getResponsavelCpf(),
            alunoModel.getMatricula()
        );
        listaAlunoDto.add(alunoDto); // Adiciona o AlunoDto na lista
    }
        return ResponseEntity.status(HttpStatus.OK).body(listaAlunoDto); // Exemplo de uso da "ResponseEntity", ele retorna um status "200 OK" se tudo ocorrer bem a requisição.
    }

    //Esse metodo pega todos os alunos do banco de dados exibindo junto o id
    @GetMapping("/admin/ids") //admin
    public ResponseEntity getAllId(){ //"ResponseEntity" é uma classe do SpringFramework que serve pra representar e modificar respostas http. 
        List<AlunoModel> listarAlunos = aRepository.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(listarAlunos); // Exemplo de uso da "ResponseEntity", ele retorna um status "200 OK" se tudo ocorrer bem a requisição.
    }

    //Metodo responsável por procurar aluno pelo Id
    @GetMapping("/admin/ids/{id}") //admin
    public ResponseEntity getById(@PathVariable(value = "id") Integer id) { //É o mesmo metodo GetMapping, tendo a diferença que necessita que um paramétro seja passado na requisição, no caso o "id"
        Optional<AlunoModel> aluno = aRepository.findById(id); //O "Optional" é uma alternativa ao uso de excessões "NullPointers", Sua principal função é representar um valor que pode ou não estar presente. Ou seja, ao inves de exigir um dado ele simplesmente declara ele como "nulo".
        if(aluno.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Aluno não encontrado"); // Se não existir aluno ele da um erro como não encontrado
        } 
        return ResponseEntity.status(HttpStatus.OK).body(aluno.get()); //"FOUND" é o tipo de status específico pra essa requisição
    }

    //Obtem informações do usuario pelo cpf
    @GetMapping("/recepcionista/cpf/{cpf}") // Recepcionista e aluno
    public ResponseEntity getByCpf(@PathVariable(value = "cpf") String cpf, @AuthenticationPrincipal UserDetails principal) {

    // Verifica se o aluno existe no banco de dados
    Optional<AlunoModel> aluno = aRepository.findByCpf(cpf);
    if (aluno.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Aluno não encontrado");
    }

    AlunoModel alunoModel = aluno.get();

    // Autenticação e autorização
    String username = principal.getUsername(); // Usuário logado 
    String matricula = alunoModel.getMatricula(); // Matrícula do aluno

    // Verifica se o usuário é o dono do CPF ou possui papel de ADMIN/RECEPCIONISTA
    if (!username.equals(matricula) && !hasRole("ADMIN", principal) && !hasRole("RECEPCIONISTA", principal)) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Você não tem permissão para acessar este CPF");
    }

    // Converte para DTO
    AlunoDto alunoDto = new AlunoDto(
        alunoModel.getNome(),
        alunoModel.getNascimento(),
        alunoModel.getEmail(),
        alunoModel.getTelefone(),
        alunoModel.getCpf(),
        alunoModel.isMenor(),
        alunoModel.getResponsavelCpf(),
        alunoModel.getMatricula()
    );

    return ResponseEntity.status(HttpStatus.OK).body(alunoDto);
}

// Método auxiliar para verificar se o usuário tem o papel especificado
private boolean hasRole(String role, UserDetails principal) {
    return principal.getAuthorities()
                    .stream()
                    .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + role));
}


    //Metodo de cadastro inicial de um aluno(função permitida para adm e recepcionistas)
    @PostMapping("/recepcionista/cadastro") //recepcionista 
    public ResponseEntity save(@Valid @RequestBody AlunoDto dto){
    // Verifica se já existe um aluno com esse email cadastrado e se o valor de email é diferente de null
    if(dto.email() != null && aRepository.findByEmail(dto.email()).isPresent()){
        return ResponseEntity.status(HttpStatus.CONFLICT).body("Já existe um aluno com este e-mail cadastrado.");
    }

    // Verifica se já existe um aluno com esse número cadastrado
    if(aRepository.findByTelefone(dto.telefone()).isPresent()){
        return ResponseEntity.status(HttpStatus.CONFLICT).body("Já existe um usuário com esse telefone cadastrado.");
    }

    // Verifica se já existe um aluno com esse CPF cadastrado e se o valor de cpf é diferente de null
    if(dto.cpf() != null && aRepository.findByCpf(dto.cpf()).isPresent()){
        return ResponseEntity.status(HttpStatus.CONFLICT).body("Já existe um usuário com esse CPF cadastrado.");
    }

    // Cria o objeto AlunoModel a partir do DTO
    var aluno = new AlunoModel(); 
    BeanUtils.copyProperties(dto, aluno); // Copia as propriedades do DTO para o aluno
    aluno.setMatricula(gerarMatricula());
    // Se o aluno for menor, associa o CPF do responsável
    if (dto.menor() && dto.responsavelCpf() != null && !dto.responsavelCpf().isEmpty()) {
        // Aqui, associamos o CPF do responsável ao campo 'responsavelCpf' no aluno
        aluno.setResponsavelCpf(dto.responsavelCpf());  // Associando diretamente o CPF
        aluno.setCpf(null);
    } else if (dto.menor()) {
        // Se for menor e não passar CPF de responsável, retorna erro
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("CPF do responsável não pode ser nulo ou vazio.");
    }

    // Salva o aluno no banco de dados
    return ResponseEntity.status(HttpStatus.CREATED).body(aRepository.save(aluno));
}
 
    //Metodo responsável por o usuario cadastrar a própria senha
   @PostMapping("/recepcionista/usuariocadastro") //recepcionista e aluno
    public ResponseEntity cadastrarSenha(@RequestBody AlunoLoginDto loginDto) {
    // Verifica se a matrícula existe
    Optional<AlunoModel> aluno = aRepository.findByMatricula(loginDto.getMatricula());
    if (aluno.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Matrícula não encontrada.");
    }

    // Verifica se o aluno já possui uma senha
    Optional<AlunoLoginModel> usuario = alRepository.findByUsername(loginDto.getMatricula());
    if (usuario.isPresent()) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Senha já cadastrada.");
    }

    // Criptografa a senha e salva no banco de dados
    String senhaCriptografada = passwordEncoder.encode(loginDto.getSenha());
    
    // Criação do usuário
    AlunoLoginModel usuarioCriado = new AlunoLoginModel();
    usuarioCriado.setUsername(loginDto.getMatricula());  // Matrícula como login
    usuarioCriado.setPassword(senhaCriptografada);  // Senha criptografada
    
    // Ajusta a role, garantindo que tenha o prefixo "ROLE_" sem isso, as roles no banco de dados não ver ser lidas corretamente pelo springboot
    String role = "ALUNO";
    if (!role.startsWith("ROLE_")){
        role = "ROLE_" + role; // Adiciona o prefixo "ROLE_"
    }
    usuarioCriado.setRole(role);  // Seta o papel pra "aluno"

    // Salva o usuário no banco
    alRepository.save(usuarioCriado);

    return ResponseEntity.status(HttpStatus.CREATED).body("Senha cadastrada com sucesso.");
}

    //Metodo responsável por atualizar os dados cadastrais dos alunos pelo id
    @PutMapping("/atualizar/{id}") //admin
    public ResponseEntity update(@PathVariable(value = "id") Integer id, @RequestBody AlunoDto dto) {
    Optional<AlunoModel> aluno = aRepository.findById(id);
    if (aluno.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Aluno não encontrado");
    }
    var alunoModel = aluno.get(); // A variável armazena as informações contidas do aluno

    // Só copia as propriedades que não são null
    if (dto.nome() != null) {
        alunoModel.setNome(dto.nome());
    }
    if (dto.email() != null) {
        alunoModel.setEmail(dto.email());
    }
    if (dto.cpf() != null) {
        alunoModel.setCpf(dto.cpf());
    }
    if (dto.telefone() != null) {
        alunoModel.setTelefone(dto.telefone());
    }
    //Tirar aninhado de ifs
    var responsavel = dto.responsavelCpf();
    // Verifica o campo "menor" e define o responsável
    if (!dto.menor()) {
        alunoModel.setResponsavelCpf(null);
    }
    if (dto.menor() && responsavel == null) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("CPF do responsável não pode ser nulo ou vazio.");
    }

    // Salva as alterações
    return ResponseEntity.status(HttpStatus.OK).body(aRepository.save(alunoModel));
}

    //Metodo responsável por atualizar os dados cadastrais dos alunos cpf
    @PutMapping("/recepcionista/atualizar/cpf/{cpf}") //recepcionista
    public ResponseEntity update(@PathVariable(value = "cpf") String cpf, @RequestBody AlunoDto dto) {
    Optional<AlunoModel> aluno = aRepository.findByCpf(cpf);
    if (aluno.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Aluno não encontrado");
    }
    var alunoModel = aluno.get();
    if (dto.nome() != null) {
        alunoModel.setNome(dto.nome());
    }
    if (dto.email() != null) {
        alunoModel.setEmail(dto.email());
    }
    if (dto.cpf() != null) {
        alunoModel.setCpf(dto.cpf());
    }
    if (dto.telefone() != null) {
        alunoModel.setTelefone(dto.telefone());
    }
    var responsavel = dto.responsavelCpf();
    if (!dto.menor()) {
        alunoModel.setResponsavelCpf(null);
    }
    if (dto.menor() && responsavel == null) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("CPF do responsável não pode ser nulo ou vazio.");
    }
    return ResponseEntity.status(HttpStatus.OK).body(aRepository.save(alunoModel));
}

    @DeleteMapping("/apagar/{id}") //admin
    public ResponseEntity delete(@PathVariable(value = "id") Integer id){
        Optional<AlunoModel> aluno = aRepository.findById(id);
        if(aluno.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Aluno não encontrado"); //Se não existir nenhum aluno com esse id para ser deletado, ele apenas informa sobre.
        }
        aRepository.delete(aluno.get());
        return ResponseEntity.status(HttpStatus.OK).body("Aluno deletado");
    }
    }
