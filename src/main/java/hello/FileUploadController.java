package hello;

import hello.storage.StorageFileNotFoundException;
import hello.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.stream.Collectors;

import static hello.Application.APP_IDENTIFIER;

@Controller
public class FileUploadController {

    @Autowired
    private final StorageService storageService;

    @Autowired
    private TCPServer tcpServer;

    @Autowired
    private TCPClient tcpClient;


    public FileUploadController(StorageService storageService) {
        this.storageService = storageService;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    tcpServer.getData();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    @GetMapping("/")
    public String listUploadedFiles(Model model) throws IOException {

        model.addAttribute("files", storageService.loadAll().map(
                fileName -> MvcUriComponentsBuilder.fromMethodName(FileUploadController.class,
                        "serveFile", fileName).build().toString())
                .collect(Collectors.toList()));

        return "uploadForm";
    }

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {


        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @PostMapping("/")
    public String handleFileUpload(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) {

        Application.logger.info(APP_IDENTIFIER + ":" + file.getOriginalFilename());
        try {
            tcpClient.sendFile(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + file.getOriginalFilename() + "!");

        return "redirect:/";
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }

}