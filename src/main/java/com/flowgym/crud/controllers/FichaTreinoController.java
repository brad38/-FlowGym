package com.flowgym.crud.controllers;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flowgym.crud.dtos.FichaTreinoDto;
import com.flowgym.crud.dtos.TipoTreinoDto;
import com.flowgym.crud.model.AlunoModel;
import com.flowgym.crud.model.FichaTreinoModel;
import com.flowgym.crud.model.TipoTreinoModel;
import com.flowgym.crud.repositories.AlunoRepository;
import com.flowgym.crud.repositories.FichaTreinoRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/fichas")
public class FichaTreinoController {

    @Autowired
    FichaTreinoRepository fRepository;

    @Autowired
    AlunoRepository aRepository;

    // Obtém a ficha de treino de um aluno específico pela matricula
    @GetMapping("/instrutor/exercicios/{matricula}") // instrutor
    public ResponseEntity<Object> getByAlunoMatricula(@PathVariable String matricula) {
        Optional<FichaTreinoModel> ficha = fRepository.findByAlunoMatricula(matricula);
        if (ficha.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ficha de treino não encontrada.");
        }

        FichaTreinoModel fichaModel = ficha.get();

        // Convertendo a lista de TipoTreinoModel para TipoTreinoDto
        List<TipoTreinoDto> tiposDto = new ArrayList<>();
        for (TipoTreinoModel tipo : fichaModel.getTiposTreino()) {
        TipoTreinoDto tipoDto = new TipoTreinoDto(
            tipo.getTipo(), // Tipo do treino (A, B, C, D)
            tipo.getExercicios() // Lista de exercícios
        );
        tiposDto.add(tipoDto);
    }

        FichaTreinoDto fichadto = new FichaTreinoDto(
        fichaModel.getAluno().getMatricula(), // A matricula do aluno
        fichaModel.getObjetivo(),
        fichaModel.getDataInicio(),
        fichaModel.getDataTermino(),
        tiposDto, // Passa a lista convertida de TipoTreinoDto
        fichaModel.getNumeroImpressoes(),
        fichaModel.getMaxImpressoes() // Passa o valor máximo de impressões
    );
        return ResponseEntity.status(HttpStatus.OK).body(fichadto);
    }

    //Obtém a ficha de treino de um aluno específico pela matrícula
    @GetMapping("/aluno/exercicios/{matricula}") // aluno
    public ResponseEntity<Object> getByMatricula(@PathVariable String matricula, Principal principal) {
    Optional<FichaTreinoModel> ficha = fRepository.findByAlunoMatricula(matricula);
    
    // Obtém o nome de usuário autenticado (username)
    String username = principal.getName();
    
    // Verifica se o username corresponde à matrícula do aluno ou se é um administrador
    if (!username.equals(matricula) && !hasRole("ADMIN", principal)) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Você só pode acessar sua própria ficha de treino ou ser um administrador.");
    }
    
    if (ficha.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ficha de treino não encontrada.");
    }

    FichaTreinoModel fichaModel = ficha.get();

    // Mapeia os tipos de treino de TipoTreinoModel para TipoTreinoDto
    List<TipoTreinoDto> tiposDto = fichaModel.getTiposTreino().stream()
            .map(tipo -> new TipoTreinoDto(tipo.getTipo(), tipo.getExercicios()))
            .toList();

    FichaTreinoDto fichaDto = new FichaTreinoDto(
            matricula,
            fichaModel.getObjetivo(),
            fichaModel.getDataInicio(),
            fichaModel.getDataTermino(),
            tiposDto, // Passa a lista de tipos já mapeada
            fichaModel.getNumeroImpressoes(),
            fichaModel.getMaxImpressoes()
    );
    return ResponseEntity.status(HttpStatus.OK).body(fichaDto);
}

    // Método auxiliar para verificar se o usuário tem uma role específica
    private boolean hasRole(String role, Principal principal) {
    if (principal instanceof Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_" + role));
    }
    return false;
}

    //Metodo responsavel por criar a ficha de treino
    @PostMapping("/instrutor/criar") //instrutor
    public ResponseEntity<Object> save(@Valid @RequestBody FichaTreinoDto dto) {
    // Verifica se o aluno existe
    Optional<AlunoModel> alunoOptional = aRepository.findByMatricula(dto.matricula());
    if (alunoOptional.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Aluno não encontrado.");
    }

    // Verifica se o aluno já tem uma ficha de treino
    Optional<FichaTreinoModel> fichaExistente = fRepository.findByAlunoMatricula(dto.matricula());
    if (fichaExistente.isPresent()) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Aluno já possui uma ficha de treino. Delete-a primeiro");
    }

    // Cria a ficha de treino e associa ao aluno
    var ficha = new FichaTreinoModel();
    BeanUtils.copyProperties(dto, ficha); // Copia os parâmetros do DTO para o modelo
    ficha.setAluno(alunoOptional.get());

    // Processa os tipos de treino e associa à ficha
    if (dto.tipos() != null && !dto.tipos().isEmpty()) {
        List<TipoTreinoModel> tiposTreino = new ArrayList<>();
        for (TipoTreinoDto tipoDto : dto.tipos()) {
            if (!"ABCD".contains(tipoDto.tipo())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Tipo de treino inválido.");
            }
            TipoTreinoModel tipo = new TipoTreinoModel();
            tipo.setTipo(tipoDto.tipo()); // Tipo "A", "B", "C", "D"
            tipo.setExercicios(tipoDto.exercicios()); // Exercícios relacionados ao tipo
            tipo.setFichaTreino(ficha); // Associa o tipo à ficha
            tiposTreino.add(tipo);
        }
        ficha.setTiposTreino(tiposTreino); // Associa os tipos à ficha
    }

    // Salva a ficha no banco de dados
    ficha = fRepository.save(ficha);

    // Mapeia os tipos de treino de TipoTreinoModel para TipoTreinoDto sem o ID
    List<TipoTreinoDto> tiposDto = ficha.getTiposTreino().stream()
            .map(tipo -> new TipoTreinoDto(tipo.getTipo(), tipo.getExercicios()))
            .toList();

    // Cria um DTO de FichaTreinoDto para retornar as informações sem o ID
    FichaTreinoDto fichaDto = new FichaTreinoDto(
            dto.matricula(),
            ficha.getObjetivo(),
            ficha.getDataInicio(),
            ficha.getDataTermino(),
            tiposDto, // Passa a lista de tipos já mapeada
            ficha.getNumeroImpressoes(),
            ficha.getMaxImpressoes()
    );

    return ResponseEntity.status(HttpStatus.CREATED).body(fichaDto);
}



    //Atualiza o treino com base no numero de matricula
    @PutMapping("/instrutor/atualizar/{matricula}") //instrutor
    public ResponseEntity atualizarExercicios(@PathVariable(value = "matricula") String matricula, @RequestBody FichaTreinoDto dto) {
        Optional<FichaTreinoModel> fichaOptional = fRepository.findByAlunoMatricula(matricula);
        if (fichaOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ficha de treino não encontrada.");
        }
        FichaTreinoModel ficha = fichaOptional.get();
        // Atualiza os tipos de treino com novos exercícios
        if (dto.tipos() != null && !dto.tipos().isEmpty()) {
            List<TipoTreinoModel> novosTipos = new ArrayList<>();
            for (TipoTreinoDto tipoDto : dto.tipos()) {
                TipoTreinoModel tipoTreino = new TipoTreinoModel();
                tipoTreino.setTipo(tipoDto.tipo());
                tipoTreino.setExercicios(tipoDto.exercicios());
                tipoTreino.setFichaTreino(ficha);
                novosTipos.add(tipoTreino);
            }

            // Remove os tipos antigos e adiciona os novos
            ficha.getTiposTreino().clear();
            ficha.getTiposTreino().addAll(novosTipos);

            // Atualiza o número máximo de impressões, se enviado
    if (dto.maxImpressoes() > 0) { // Apenas atualiza se o valor for positivo
        ficha.setMaxImpressoes(dto.maxImpressoes());
    }
        fRepository.save(ficha);
    }
    return ResponseEntity.status(HttpStatus.OK).body(fRepository.save(ficha));
}

    // Método responsável por imprimir o treino
    @PutMapping("/imprimir/{matricula}") // aluno
    public ResponseEntity<Object> imprimirFicha(
    @PathVariable String matricula,
    @AuthenticationPrincipal UserDetails principal) {

    // Verifica se a ficha de treino existe no banco de dados
    Optional<FichaTreinoModel> fichaOptional = fRepository.findByAlunoMatricula(matricula);
    if (fichaOptional.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ficha de treino não encontrada.");
    }

    FichaTreinoModel ficha = fichaOptional.get();

    // Autenticação e autorização
    String username = principal.getUsername(); // Usuário logado (pode ser matrícula)
    
    // Verifica se o usuário é o dono da matrícula ou possui papel de ADMIN
    if (!username.equals(matricula) && !hasRole("ADMIN", principal)) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Você não tem permissão para imprimir esta ficha de treino.");
    }

    // Incrementa o contador de impressões
    ficha.incrementarImpressao();

    // Salva a ficha com o contador de impressões atualizado
    fRepository.save(ficha);

    // Mapeia os tipos de treino de TipoTreinoModel para TipoTreinoDto
    List<TipoTreinoDto> tiposDto = ficha.getTiposTreino().stream()
            .map(tipo -> new TipoTreinoDto(tipo.getTipo(), tipo.getExercicios()))
            .toList();

    // Cria o DTO de resposta
    FichaTreinoDto fichaDto = new FichaTreinoDto(
            matricula,
            ficha.getObjetivo(),
            ficha.getDataInicio(),
            ficha.getDataTermino(),
            tiposDto, // Passa a lista de tipos já mapeada
            ficha.getNumeroImpressoes(),
            ficha.getMaxImpressoes()
    );

    return ResponseEntity.ok(fichaDto);
}

// Método auxiliar para verificar se o usuário tem o papel especificado
private boolean hasRole(String role, UserDetails principal) {
    return principal.getAuthorities()
                    .stream()
                    .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + role));
}

    // Deleta o treino
   @DeleteMapping("/instrutor/{matricula}")
    public ResponseEntity<Object> delete(@PathVariable String matricula) {
    // Busca todas as fichas associadas à matrícula
    List<FichaTreinoModel> fichas = fRepository.findAllByAlunoMatricula(matricula);

    // Verifica se existem fichas para a matrícula
    if (fichas.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nenhuma ficha de treino encontrada para a matrícula fornecida.");
    }

    // Exclui todas as fichas encontradas
    fRepository.deleteAll(fichas);

    return ResponseEntity.status(HttpStatus.OK).body("Todas as fichas de treino associadas foram deletadas com sucesso.");
}

}

//Outra grupo de classes/tabela "tipo de treino" tem que ser criada, essa tabela vai ter tipo de treinos como: A,B,C,D,E. Cada letra representa o treino do dia do aluno
//e cada letra terá uma ficha de exercicio com diferentes exercícios. Ex: Treino A{Tração frontal, Remada serrote, Rosca direta, Rosca scott. 3x12 Repetições. Tempo de descanso 1:30 minutos.}

