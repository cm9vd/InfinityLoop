package jpp.infinityloop.game.board;

import jpp.infinityloop.BlobCoder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FilesGenerator implements BoardGenerator {

    private Path path;

    public FilesGenerator(Path path) {
        this.path = path;
    }

    @Override
    public Board next() {
        byte[] fileContents;
        try {
            fileContents = Files.readAllBytes(path);
        } catch (IOException io){
            //System.err.println("Invalid path or file.");
            return null;
        }

        return BlobCoder.decode(fileContents);
    }

    /*@Override
    public boolean hasNext() {
        return false;
    }*/

    // test
    public static void main(String[] args) {


        /*Path path = Paths.get("Levels/24_sample_invalid_wrong_width.bin");
        byte[] fileContents;
        try {
            fileContents = Files.readAllBytes(path);
        } catch (Exception io){
            throw new IllegalArgumentException("error");
        }

        BlobCoder.decode(fileContents);

        FilesGenerator tester = new FilesGenerator("test.bin");
        tester.next();*/

    }
}
