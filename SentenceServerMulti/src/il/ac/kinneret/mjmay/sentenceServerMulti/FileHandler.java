package il.ac.kinneret.mjmay.sentenceServerMulti;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;

public class FileHandler {
    private HashMap<String, FileDTO> FilesStorage;

//    private HashMap<String, FileDTO> FilesStorage = new HashMap<String, FileDTO>();

    FileHandler(){
        this.FilesStorage = new HashMap<String, FileDTO>();
    }

    protected FileDTO AddFile(String fileName) {
        FileDTO file = this.FilesStorage.get(fileName);
        if(file == null){
            FileDTO newFile = new FileDTO();
            newFile.isLocked = false;
            FilesStorage.put(fileName, newFile);
            FileDTO test = FilesStorage.get(fileName);
            System.out.println("File added" + fileName + test.isLocked);
            return newFile;
        }
        return null;
    }

    protected FileDTO LockFile(String fileName) {
        FileDTO file = this.FilesStorage.get(fileName);
        if(file != null){
            file.isLocked = true;
            FilesStorage.put(fileName, file);
            return file;
        }
        return null;
    }

    protected FileDTO GetFile(String fileName) {
        FileDTO file = this.FilesStorage.get(fileName);
        if(file != null){
            return file;
        }
        return null;
    }

    public static String FileToJson(FileDTO file) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        System.out.println(gson.toJson(file));
        return gson.toJson(file);
    }


}
