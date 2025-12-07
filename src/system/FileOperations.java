package system;
public interface FileOperations {
    void saveToFile(String filename) throws Exception;
    void loadFromFile(String filename) throws Exception;
}
