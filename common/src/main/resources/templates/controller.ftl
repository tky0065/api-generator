package ${entity.packageName}.web.rest;

import ${entity.packageName}.service.${entity.className}Service;
import ${entity.packageName}.service.dto.${entity.className}DTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;

@RestController
@RequestMapping("${apiPath}")
@Tag(name = "${entity.className} Management")
@RequiredArgsConstructor
public class ${entity.className}Resource {

private final ${entity.className}Service ${entity.className?uncap_first}Service;

@PostMapping
@Operation(summary = "Create a new ${entity.className}")
public ResponseEntity<${entity.className}DTO> create(@RequestBody ${entity.className}DTO dto) {
    ${entity.className}DTO result = ${entity.className?uncap_first}Service.save(dto);
    return ResponseEntity.created(URI.create("/${apiPath}/" + result.getId())).body(result);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing ${entity.className}")
    public ResponseEntity<${entity.className}DTO> update(@PathVariable ${idField.type} id, @RequestBody ${entity.className}DTO dto) {
        return ResponseEntity.ok(${entity.className?uncap_first}Service.update(dto));
        }

        @GetMapping("/{id}")
        @Operation(summary = "Get a ${entity.className} by id")
        public ResponseEntity<${entity.className}DTO> getOne(@PathVariable ${idField.type} id) {
            return ${entity.className?uncap_first}Service.findOne(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
            }

            @DeleteMapping("/{id}")
            @Operation(summary = "Delete a ${entity.className}")
            public ResponseEntity<Void> delete(@PathVariable ${idField.type} id) {
                ${entity.className?uncap_first}Service.delete(id);
                return ResponseEntity.noContent().build();
                }
                }