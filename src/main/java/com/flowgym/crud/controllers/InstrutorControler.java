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
import com.flowgym.crud.dtos.CpfLoginDto;
import com.flowgym.crud.dtos.InstrutorDto;
import com.flowgym.crud.model.AlunoLoginModel;
import com.flowgym.crud.model.InstrutorModel;
import com.flowgym.crud.repositories.InstrutorRepository;
import com.flowgym.crud.repositories.LoginRepository;


import jakarta.validation.Valid;

@RestController
@RequestMapping("/instrutor")
public class InstrutorControler {

    @Autowired
    InstrutorRepository iRepository;

    @Autowired
    private LoginRepository alRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;    

    //Metodo pega todos os instrutores do banco, mas sem exibir os id
    @GetMapping //Recepcionista e adm
    public ResponseEntity getAll(){
        List<InstrutorModel> listarInstrutor = iRepository.findAll();
        
        List<InstrutorDto> listarInstrutorDto = new ArrayList<>();
        for (InstrutorModel instrutorModel : listarInstrutor){
            InstrutorDto instrutorDto = new InstrutorDto(
            instrutorModel.getNome(),
            instrutorModel.getCpf(),
            instrutorModel.getEmail(),
            instrutorModel.getTelefone()
            );
        listarInstrutorDto.add(instrutorDto);
        }
        return ResponseEntity.status(HttpStatus.OK).body(listarInstrutorDto);
    }

    //Metodo responsavel por listar os instrutores exibindo junto os ids
    @GetMapping("/admin/ids") //admin
    public ResponseEntity getAllId(){ //"ResponseEntity" é uma classe do SpringFramework que serve pra representar e modificar respostas http. 
        List<InstrutorModel> listarInstrutores = iRepository.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(listarInstrutores); // Exemplo de uso da "ResponseEntity", ele retorna um status "200 OK" se tudo ocorrer bem a requisição.
    }

    //Metodo responsável por procurar instrutor pelo Id
    @GetMapping("/admin/ids/{id}") //admin
    public ResponseEntity getById(@PathVariable(value = "id") Integer id) { 
        Optional<InstrutorModel> instrutor = iRepository.findById(id); 
        if(instrutor.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Instrutor não encontrado");
        } 
        return ResponseEntity.status(HttpStatus.OK).body(instrutor.get());
    }

    //Método responsável por procurar instrutor pelo CPF
    @GetMapping("/recepcionista/cpf/{cpf}") // recepcionista e instrutor
    public ResponseEntity<Object> getByCpf(
    @PathVariable(value = "cpf") String cpf, 
    @AuthenticationPrincipal UserDetails principal) {

    // Verifica se o instrutor existe no banco de dados
    Optional<InstrutorModel> instrutor = iRepository.findByCpf(cpf);
    if (instrutor.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Instrutor não encontrado");
    }

    InstrutorModel instrutorModel = instrutor.get();

    // Autenticação e autorização
    String username = principal.getUsername(); // Usuário logado (pode ser CPF)
    
    // Verifica se o usuário é o dono do CPF ou possui papel de ADMIN/RECEPCIONISTA
    if (!username.equals(cpf) && !hasRole("ADMIN", principal) && !hasRole("RECEPCIONISTA", principal)) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Você não tem permissão para acessar este CPF");
    }

    InstrutorDto instrutorDto = new InstrutorDto(
            instrutorModel.getNome(),
            instrutorModel.getEmail(),
            instrutorModel.getTelefone(),
            instrutorModel.getCpf()
    );
    return ResponseEntity.status(HttpStatus.OK).body(instrutorDto);
}

private boolean hasRole(String role, UserDetails principal) {
    return principal.getAuthorities()
                    .stream()
                    .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + role));
}


    //Metodo de cadastro inicial de um instrutor(função permitida para adm e recepcionistas)
    @PostMapping("/recepcionista/cadastro") //recepcionista 
    public ResponseEntity save(@Valid @RequestBody InstrutorDto dto){
    
    if(dto.email() != null && iRepository.findByEmail(dto.email()).isPresent()){
        return ResponseEntity.status(HttpStatus.CONFLICT).body("Já existe um usuário com este e-mail cadastrado.");
    }

    if(iRepository.findByTelefone(dto.telefone()).isPresent()){
        return ResponseEntity.status(HttpStatus.CONFLICT).body("Já existe um usuário com esse telefone cadastrado.");
    }

    if(dto.cpf() != null && iRepository.findByCpf(dto.cpf()).isPresent()){
        return ResponseEntity.status(HttpStatus.CONFLICT).body("Já existe um usuário com esse CPF cadastrado.");
    }

    var instrutor = new InstrutorModel(); 
    BeanUtils.copyProperties(dto, instrutor); 

    return ResponseEntity.status(HttpStatus.CREATED).body(iRepository.save(instrutor));
}

    //Metodo responsável pelo instrutor criar a senha dele
    @PostMapping("/recepcionista/usuariocadastro") //recepcionista e instrutor
    public ResponseEntity cadastrarSenha(@RequestBody CpfLoginDto loginDto) {

    Optional<InstrutorModel> instrutor = iRepository.findByCpf(loginDto.getCpf());
    if (instrutor.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Instrutor não encontrado.");
    }

    Optional<AlunoLoginModel> usuario = alRepository.findByUsername(loginDto.getCpf());
    if (usuario.isPresent()) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Senha já cadastrada.");
    }

    String senhaCriptografada = passwordEncoder.encode(loginDto.getSenha());

    AlunoLoginModel usuarioCriado = new AlunoLoginModel();
    usuarioCriado.setUsername(loginDto.getCpf());  
    usuarioCriado.setPassword(senhaCriptografada); 
 
    String role = "INSTRUTOR";
    if (!role.startsWith("ROLE_")) {
        role = "ROLE_" + role; 
    }
    usuarioCriado.setRole(role);  

    alRepository.save(usuarioCriado);

    return ResponseEntity.status(HttpStatus.CREATED).body("Senha cadastrada com sucesso.");
}

    //Metodo responsável por atualizar os dados cadastrais dos instrutores pelo id
    @PutMapping("/atualizar/{id}") //admin
    public ResponseEntity update(@PathVariable(value = "id") Integer id, @RequestBody AlunoDto dto) {
    Optional<InstrutorModel> instrutor = iRepository.findById(id);
    if (instrutor.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Instrutor não encontrado");
    }
    var instrutorModel = instrutor.get();

    if (dto.nome() != null) {
        instrutorModel.setNome(dto.nome());
    }
    if (dto.email() != null) {
        instrutorModel.setEmail(dto.email());
    }
    if (dto.cpf() != null) {
        instrutorModel.setCpf(dto.cpf());
    }
    if (dto.telefone() != null) {
        instrutorModel.setTelefone(dto.telefone());
    }

    // Salva as alterações
    return ResponseEntity.status(HttpStatus.OK).body(iRepository.save(instrutorModel));
}

    //Metodo responsável por atualizar os dados cadastrais dos instrutores pelo cpf
    @PutMapping("/recepcionista/atualizar/cpf/{cpf}") //recepcionista
    public ResponseEntity update(@PathVariable(value = "cpf") String cpf, @RequestBody AlunoDto dto) {
    Optional<InstrutorModel> instrutor = iRepository.findByCpf(cpf);
    if (instrutor.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Instrutor não encontrado");
    }
    var instrutorModel = instrutor.get();
    if (dto.nome() != null) {
        instrutorModel.setNome(dto.nome());
    }
    if (dto.email() != null) {
        instrutorModel.setEmail(dto.email());
    }
    if (dto.cpf() != null) {
        instrutorModel.setCpf(dto.cpf());
    }
    if (dto.telefone() != null) {
        instrutorModel.setTelefone(dto.telefone());
    }
    return ResponseEntity.status(HttpStatus.OK).body(iRepository.save(instrutorModel));
}
     //Metodo responsável por deletar instrutor pelo id
    @DeleteMapping("/apagar/{id}") //admin
    public ResponseEntity delete(@PathVariable(value = "id") Integer id){
        Optional<InstrutorModel> instrutor = iRepository.findById(id);
        if(instrutor.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Instrutor não encontrado"); 
        }
        iRepository.delete(instrutor.get());
        return ResponseEntity.status(HttpStatus.OK).body("Instrutor deletado");
    }

     //Metodo responsável por deletar instrutor pelo cpf
    @DeleteMapping("/apagar/cpf/{cpf}") //admin
    public ResponseEntity delete(@PathVariable(value = "cpf") String cpf){
        Optional<InstrutorModel> instrutor = iRepository.findByCpf(cpf);
        if(instrutor.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Instrutor não encontrado"); 
        }
        iRepository.delete(instrutor.get());
        return ResponseEntity.status(HttpStatus.OK).body("Instrutor deletado");
    }
}
