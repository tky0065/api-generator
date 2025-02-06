@RestController
@RequestMapping("${apiPath}/files")
@Tag(name = "${entity.className} File Management")
@RequiredArgsConstructor
public class ${entity.className}FileResource {

private final ${entity.className}Service ${entity.className?uncap_first}Service;

@PostMapping("/upload/{id}")
@Operation(summary = "Upload a file for ${entity.className}")
public ResponseEntity<${entity.className}DTO> uploadFile(@PathVariable ${idField.type} id, @RequestParam("file") MultipartFile file) {
    return ResponseEntity.ok(${entity.className?uncap_first}Service.uploadFile(id, file));
    }

    @GetMapping("/download/{id}")
    @Operation(summary = "Download a file from ${entity.className}")
    public ResponseEntity<Resource> downloadFile(@PathVariable ${idField.type} id) {
        return ${entity.className?uncap_first}Service.downloadFile(id)
        .map(resource -> ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
        .body(resource))
        .orElse(ResponseEntity.notFound().build());
        }
        }