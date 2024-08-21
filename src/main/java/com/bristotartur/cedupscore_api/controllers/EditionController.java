package com.bristotartur.cedupscore_api.controllers;

import com.bristotartur.cedupscore_api.dtos.response.EditionResponseDto;
import com.bristotartur.cedupscore_api.enums.Status;
import com.bristotartur.cedupscore_api.services.EditionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/editions")
@RequiredArgsConstructor
@Transactional
public class EditionController {

    private final EditionService editionService;

    @GetMapping
    public ResponseEntity<Page<EditionResponseDto>> listAllEditions(Pageable pageable) {
        var editions = editionService.findAllEditions(pageable);
        var dtos = editions.stream()
                .map(editionService::createEditionResponseDto)
                .toList();

        return ResponseEntity.ok().body(new PageImpl<>(dtos, editions.getPageable(), editions.getSize()));
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<EditionResponseDto> findEditionById(@PathVariable Long id) {
        var edition = editionService.findEditionById(id);
        return ResponseEntity.ok().body(editionService.createEditionResponseDto(edition));
    }

    @GetMapping(path = "/from")
    public ResponseEntity<EditionResponseDto> findEditionByYear(@RequestParam("year") Integer year) {
        var edition = editionService.findEditionByYear(year);
        return ResponseEntity.ok().body(editionService.createEditionResponseDto(edition));
    }

    @PostMapping(path = "/open-edition")
    public ResponseEntity<EditionResponseDto> saveEdition() {
        var edition = editionService.openNewEdition();
        return ResponseEntity.status(HttpStatus.CREATED).body(editionService.createEditionResponseDto(edition));
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> deleteEdition(@PathVariable Long id) {
        editionService.deleteEdition(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(path = "/{id}/update")
    public ResponseEntity<EditionResponseDto> updateEditionStatus(@PathVariable Long id,
                                                                  @RequestParam("status") String status) {
        var fotmattedStatus = Status.findStatusLike(status);
        var edition = editionService.updateEditionStatus(id, fotmattedStatus);

        return ResponseEntity.ok().body(editionService.createEditionResponseDto(edition));
    }

}
