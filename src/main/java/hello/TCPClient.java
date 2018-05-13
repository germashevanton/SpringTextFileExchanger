package hello;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.*;

@Component
public class TCPClient {

    public void sendFile(MultipartFile multipartFile) throws Exception {
        Socket sock = new Socket("localhost", 13267);

        //Send file

        File file = convert(multipartFile);
        byte[] myByteArray = new byte[(int) file.length()];

        FileInputStream fis = new FileInputStream(file);
        BufferedInputStream bis = new BufferedInputStream(fis);


        DataInputStream dis = new DataInputStream(bis);
        dis.readFully(myByteArray, 0, myByteArray.length);

        OutputStream os = sock.getOutputStream();

        //Sending file name and file size to the server
        DataOutputStream dos = new DataOutputStream(os);
        dos.writeUTF(file.getName());
        dos.writeLong(myByteArray.length);
        dos.write(myByteArray, 0, myByteArray.length);
        dos.flush();

        //Sending file data to the server
        os.write(myByteArray, 0, myByteArray.length);
        os.flush();

        //Closing socket
        sock.close();
    }

    public void sendRequestOnRequiredFile(String fileName){
        return;
    }

    private File convert(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        convFile.createNewFile();
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }
}