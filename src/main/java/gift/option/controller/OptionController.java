package gift.option.controller;

import gift.option.dto.OptionAddRequest;
import gift.option.dto.OptionResponse;
import gift.option.service.OptionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products/{productId}/options")
public class OptionController {

    private final OptionService optionService;

    public OptionController(OptionService optionService) {
        this.optionService = optionService;
    }

    @PostMapping
    public ResponseEntity<OptionResponse> addOption(
        @PathVariable Long productId,
        @Valid @RequestBody OptionAddRequest optionAddRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(optionService.addOption(productId, optionAddRequest));
    }
}
