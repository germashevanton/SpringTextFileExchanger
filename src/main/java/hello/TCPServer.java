package hello;

import hello.storage.FileSystemStorageService;
import hello.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.mock.web.MockMultipartFile;
import sun.misc.IOUtils;

import java.io.*;
import java.net.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static hello.Application.APP_IDENTIFIER;

@Component
class TCPServer {

    @Autowired
    private FileSystemStorageService fileSystemStorageService;

    private final StorageService storageService;



    public TCPServer(StorageService storageService){
        this.storageService = storageService;

        try {
            getData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getData() throws IOException {

        int bytesRead;
        int current = 0;

        ServerSocket serverSocket = null;
        serverSocket = new ServerSocket(13267);

        while(true) {
            Socket clientSocket = null;
            clientSocket = serverSocket.accept();

            InputStream in = clientSocket.getInputStream();

            DataInputStream clientData = new DataInputStream(in);

            String fileName = clientData.readUTF();

            List<String> list = new ArrayList<>();
            list = (List<String>) fileSystemStorageService.loadAll();
            if (!list.contains(fileName) && fileName != null){
                converAndSaveFile(in, fileName);
            } else {
                fileSystemStorageService.file = streamToPathConverter(in, fileName);
            }
        }
    }



    private void converAndSaveFile(InputStream in, String fileName){

        try {
            MultipartFile multipartFile = new MockMultipartFile(fileName, in);
            storageService.store(multipartFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private Path streamToPathConverter(InputStream in, String fileName){
        try {
            MultipartFile multipartFile = new MockMultipartFile(fileName, in);
            File convFile = new File( multipartFile.getOriginalFilename());
            multipartFile.transferTo(convFile);
            return convFile.toPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
