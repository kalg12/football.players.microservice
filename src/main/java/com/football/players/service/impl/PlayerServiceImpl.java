package com.football.players.service.impl;

import com.football.players.dto.PlayerDto;
import com.football.players.entity.Player;
import com.football.players.exception.ResourceNotFoundException;
import com.football.players.messaging.PlayerEventPublisher;
import com.football.players.repository.PlayerRepository;
import com.football.players.service.PlayerService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación de PlayerService.
 * Inyectamos PlayerEventPublisher para emitir mensaje tras crear.
 */
@Service
public class PlayerServiceImpl implements PlayerService {

    private final PlayerRepository repo;
    private final PlayerEventPublisher publisher;

    // ① Inyectamos repositorio y publisher
    public PlayerServiceImpl(PlayerRepository repo,
                             PlayerEventPublisher publisher) {
        this.repo = repo;
        this.publisher = publisher;
    }

    @Override
    public List<PlayerDto> findAll() {
        return repo.findAll().stream()
                   .map(this::toDto)
                   .collect(Collectors.toList());
    }

    @Override
    public PlayerDto findById(Long id) {
        Player p = repo.findById(id)
                       .orElseThrow(() -> new ResourceNotFoundException("Player id " + id + " no encontrado"));
        return toDto(p);
    }

    @Override
    public PlayerDto create(PlayerDto dto) {
        // ② guardamos en BD
        Player p = new Player();
        p.setName(dto.name());
        p.setPosition(dto.position());
        Player saved = repo.save(p);

        // ③ mapeamos a DTO
        PlayerDto result = toDto(saved);

        // ④ publicamos el evento
        publisher.publishPlayerCreated(result);

        // ⑤ retornamos
        return result;
    }

    @Override
    public PlayerDto update(Long id, PlayerDto dto) {
        Player p = repo.findById(id)
                       .orElseThrow(() -> new ResourceNotFoundException("Player id " + id + " no encontrado"));
        p.setName(dto.name());
        p.setPosition(dto.position());
        return toDto(repo.save(p));
    }

    @Override
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new ResourceNotFoundException("Player id " + id + " no encontrado");
        }
        repo.deleteById(id);
    }

    private PlayerDto toDto(Player p) {
        return new PlayerDto(p.getId(), p.getName(), p.getPosition());
    }
}