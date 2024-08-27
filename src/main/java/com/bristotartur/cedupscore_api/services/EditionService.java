package com.bristotartur.cedupscore_api.services;

import com.bristotartur.cedupscore_api.domain.Edition;
import com.bristotartur.cedupscore_api.dtos.response.EditionResponseDto;
import com.bristotartur.cedupscore_api.enums.Status;
import com.bristotartur.cedupscore_api.exceptions.ConflictException;
import com.bristotartur.cedupscore_api.exceptions.NotFoundException;
import com.bristotartur.cedupscore_api.exceptions.UnprocessableEntityException;
import com.bristotartur.cedupscore_api.mappers.EditionMapper;
import com.bristotartur.cedupscore_api.mappers.ScoreMapper;
import com.bristotartur.cedupscore_api.repositories.EditionRepository;
import com.bristotartur.cedupscore_api.repositories.TeamScoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional
public class EditionService {

    private final EditionRepository editionRepository;
    private final TeamScoreRepository teamScoreRepository;
    private final EditionMapper editionMapper;
    private final ScoreMapper scoreMapper;
    private final TeamService teamService;

    public Page<Edition> findAllEditions(Pageable pageable) {
        return editionRepository.findAll(pageable);
    }

    public Edition findEditionById(Long id) {

        return editionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Edição não encontrada."));
    }

    public Edition findEditionByYear(int year) {

        return editionRepository.findByYear(year)
                .orElseThrow(() -> new NotFoundException("Edição não encontrada."));
    }

    public EditionResponseDto createEditionResponseDto(Edition edition) {
        var teamScores = teamScoreRepository.findAllByEdition(edition)
                .stream()
                .map(score -> {
                    var teamDto = teamService.createTeamResponseDto(score.getTeam());
                    return scoreMapper.toTeamScoreResponseDto(score, teamDto);
                }).toList();

        return editionMapper.toEditionResponseDto(edition, teamScores);
    }

    public Edition openNewEdition() {
        var currentDate = LocalDate.now();
        this.checkYear(currentDate.getYear());

        var edition = editionRepository.save(Edition.builder()
                .startDate(currentDate)
                .closingDate(currentDate)
                .status(Status.SCHEDULED)
                .build()
        );
        var teams = teamService.findAllActiveTeams();

        teams.forEach(team -> {
            var score = scoreMapper.toNewTeamScore(0, edition, team);
            teamScoreRepository.save(score);
        });
        return edition;
    }

    public void deleteEdition(Long id) {
        var edition = this.findEditionById(id);

        if (!edition.getStatus().equals(Status.SCHEDULED)) {
            throw new UnprocessableEntityException("A edição não pode ser removida.");
        }
        if (!edition.getEvents().isEmpty()) {
            throw new UnprocessableEntityException("A edição não pode ser removida.");
        }
        editionRepository.delete(edition);
    }

    public Edition updateEditionStatus(Long id, Status status) {
        var edition = this.findEditionById(id);

        Status.checkStatus(edition.getStatus(), status);

        if (edition.getStatus().equals(Status.SCHEDULED) && status.equals(Status.IN_PROGRESS)) {
            edition.setStartDate(LocalDate.now());
            edition.setClosingDate(LocalDate.now());
        }
        if (status.equals(Status.ENDED) || status.equals(Status.CANCELED)) {
            edition.setClosingDate(LocalDate.now());
        }
        edition.setStatus(status);
        return editionRepository.save(edition);
    }

    private void checkYear(int year) {
        editionRepository.findByYear(year).ifPresent(e -> {
            throw new ConflictException("Já existe uma edição no ano '%d'.".formatted(year));
        });
    }

}
