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
import com.flowgym.crud.dtos.RecepcionistaDto;
import com.flowgym.crud.model.AlunoLoginModel;
import com.flowgym.crud.model.InstrutorModel;
import com.flowgym.crud.model.RecepcionistaModel;
import com.flowgym.crud.repositories.InstrutorRepository;
import com.flowgym.crud.repositories.LoginRepository;
import com.flowgym.crud.repositories.RecepcionistaRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/recepcionista")
public class RecepcionistaController {
    @Autowired
    RecepcionistaRepository rRepository;

    @Autowired
    private LoginRepository alRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;   

    //Metodo responsavel por listar recepcionista exibindo junto os ids
    @GetMapping("/admin/ids") //admin
    public ResponseEntity getAllId(){ //"ResponseEntity" é uma classe do SpringFramework que serve pra representar e modificar respostas http. 
        List<RecepcionistaModel> listarRecepcionistas = rRepository.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(listarRecepcionistas); // Exemplo de uso da "ResponseEntity", ele retorna um status "200 OK" se tudo ocorrer bem a requisição.
    }

    //Metodo responsável por procurar recepcionista pelo Id
    @GetMapping("/admin/ids/{id}") //admin
    public ResponseEntity getById(@PathVariable(value = "id") Integer id) { 
        Optional<RecepcionistaModel> recepcionista = rRepository.findById(id); 
        if(recepcionista.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Recepcionista não encontrado");
        } 
        return ResponseEntity.status(HttpStatus.OK).body(recepcionista.get());
    }

    // Método responsável por procurar recepcionista pelo CPF
@GetMapping("/recepcionista/cpf/{cpf}") // recepcionista e admin
public ResponseEntity getByCpf(
    @PathVariable(value = "cpf") String cpf,
    @AuthenticationPrincipal UserDetails principal) {

    // Verifica se o recepcionista existe no banco de dados
    Optional<RecepcionistaModel> recepcionista = rRepository.findByCpf(cpf);
    if (recepcionista.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Recepcionista não encontrado");
    }

    RecepcionistaModel recepcionistaModel = recepcionista.get();

    // Autenticação e autorização
    String username = principal.getUsername(); // Usuário logado (pode ser CPF)
    String recepcionistaCpf = recepcionistaModel.getCpf(); // CPF do recepcionista

    // Verifica se o usuário é o dono do CPF ou possui papel de ADMIN
    if (!username.equals(recepcionistaCpf) && !hasRole("ADMIN", principal)) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Você não tem permissão para acessar este CPF");
    }

    // Converte para DTO
    RecepcionistaDto recepcionistaDto = new RecepcionistaDto(
            recepcionistaModel.getNome(),
            recepcionistaModel.getEmail(),
            recepcionistaModel.getTelefone(),
            recepcionistaModel.getCpf()
    );

    return ResponseEntity.status(HttpStatus.OK).body(recepcionistaDto);
}

// Método auxiliar para verificar se o usuário tem o papel especificado
private boolean hasRole(String role, UserDetails principal) {
    return principal.getAuthorities()
                    .stream()
                    .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + role));
}


    //Metodo de cadastro inicial de uma recepcionista
    @PostMapping("/recepcionista/cadastro") //admin 
    public ResponseEntity save(@Valid @RequestBody RecepcionistaDto dto){
    
    if(dto.email() != null && rRepository.findByEmail(dto.email()).isPresent()){
        return ResponseEntity.status(HttpStatus.CONFLICT).body("Já existe um usuário com este e-mail cadastrado.");
    }

    if(rRepository.findByTelefone(dto.telefone()).isPresent()){
        return ResponseEntity.status(HttpStatus.CONFLICT).body("Já existe um usuário com esse telefone cadastrado.");
    }

    if(dto.cpf() != null && rRepository.findByCpf(dto.cpf()).isPresent()){
        return ResponseEntity.status(HttpStatus.CONFLICT).body("Já existe um usuário com esse CPF cadastrado.");
    }

    var recepcionista = new RecepcionistaModel(); 
    BeanUtils.copyProperties(dto, recepcionista); 

    return ResponseEntity.status(HttpStatus.CREATED).body(rRepository.save(recepcionista));
}

    //Metodo responsável pelo recepcionista criar a senha dele
    @PostMapping("/recepcionista/usuariocadastro") //admin e recepcionista
    public ResponseEntity cadastrarSenha(@RequestBody CpfLoginDto loginDto) {

    Optional<RecepcionistaModel> recepcionista = rRepository.findByCpf(loginDto.getCpf());
    if (recepcionista.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Recepcionista não encontrado.");
    }

    Optional<AlunoLoginModel> usuario = alRepository.findByUsername(loginDto.getCpf());
    if (usuario.isPresent()) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Senha já cadastrada.");
    }

    String senhaCriptografada = passwordEncoder.encode(loginDto.getSenha());

    AlunoLoginModel usuarioCriado = new AlunoLoginModel();
    usuarioCriado.setUsername(loginDto.getCpf());  
    usuarioCriado.setPassword(senhaCriptografada); 
 
    String role = "RECEPCIONISTA";
    if (!role.startsWith("ROLE_")) {
        role = "ROLE_" + role; 
    }
    usuarioCriado.setRole(role);  

    alRepository.save(usuarioCriado);

    return ResponseEntity.status(HttpStatus.CREATED).body("Senha cadastrada com sucesso.");
}

    //Metodo responsável por atualizar os dados cadastrais dos recepcionista pelo id
    @PutMapping("/atualizar/{id}") //admin
    public ResponseEntity update(@PathVariable(value = "id") Integer id, @RequestBody AlunoDto dto) {
    Optional<RecepcionistaModel> recepcionista = rRepository.findById(id);
    if (recepcionista.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Recepcionista não encontrado");
    }
    var recepcionistaModel = recepcionista.get();

    if (dto.nome() != null) {
        recepcionistaModel.setNome(dto.nome());
    }
    if (dto.email() != null) {
        recepcionistaModel.setEmail(dto.email());
    }
    if (dto.cpf() != null) {
        recepcionistaModel.setCpf(dto.cpf());
    }
    if (dto.telefone() != null) {
        recepcionistaModel.setTelefone(dto.telefone());
    }

    // Salva as alterações
    return ResponseEntity.status(HttpStatus.OK).body(rRepository.save(recepcionistaModel));
}
    //Metodo responsável por atualizar os dados cadastrais dos recepcionistas pelo cpf
    @PutMapping("/recepcionista/atualizar/cpf/{cpf}") //recepcionista
    public ResponseEntity update(@PathVariable(value = "cpf") String cpf, @RequestBody AlunoDto dto) {
    Optional<RecepcionistaModel> recepcionista = rRepository.findByCpf(cpf);
    if (recepcionista.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Recepcionista não encontrado");
    }
    var recepcionistaModel = recepcionista.get();
    if (dto.nome() != null) {
        recepcionistaModel.setNome(dto.nome());
    }
    if (dto.email() != null) {
        recepcionistaModel.setEmail(dto.email());
    }
    if (dto.cpf() != null) {
        recepcionistaModel.setCpf(dto.cpf());
    }
    if (dto.telefone() != null) {
        recepcionistaModel.setTelefone(dto.telefone());
    }
    return ResponseEntity.status(HttpStatus.OK).body(rRepository.save(recepcionistaModel));
}

    //Metodo responsável por deletar recepcionista pelo id
    @DeleteMapping("/apagar/{id}") //admin
    public ResponseEntity delete(@PathVariable(value = "id") Integer id){
        Optional<RecepcionistaModel> recepcionista = rRepository.findById(id);
        if(recepcionista.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Recepcionista não encontrado"); 
        }
        rRepository.delete(recepcionista.get());
        return ResponseEntity.status(HttpStatus.OK).body("Recepcionista deletado");
    }

    //Metodo responsável por deletar recepcionista pelo cpf
    @DeleteMapping("/apagar/cpf/{cpf}") //admin
    public ResponseEntity delete(@PathVariable(value = "cpf") String cpf){
        Optional<RecepcionistaModel> recepcionista = rRepository.findByCpf(cpf);
        if(recepcionista.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Recepcionista não encontrado"); 
        }
        rRepository.delete(recepcionista.get());
        return ResponseEntity.status(HttpStatus.OK).body("Recepcionista deletado");
    }


}
