package org.pseudonymous.plagiarism.processing;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.codehaus.plexus.util.DirectoryScanner;
import org.pseudonymous.plagiarism.config.Configs;
import org.pseudonymous.plagiarism.config.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by pseudonymous.
 */
public class FileConverter {
    private static final Tika parser = new Tika();

    private static String parseFile(File toParse) throws IOException {
        try {
            return parser.parseToString(toParse);
        } catch (TikaException error) {
            throw new IOException("Failed to parse file " + toParse.getAbsoluteFile());
        }
    }

    private static String fixFolderPath(String path) {
        if (!path.endsWith(File.separator)) {
            path += File.separator;
        }
        return path;
    }

    public static boolean checkIfFolderExists(String path) {
        return new File(path).isDirectory();
    }

    public static boolean checkIfFileExists(String filePath) {
        return new File(filePath).isFile();
    }

    public static List<Pair<String, String>> processFolder(String folderPath) {
        List<Pair<String, String>> fileList = new ArrayList<>();

        final DirectoryScanner scanner = new DirectoryScanner();
        scanner.setIncludes(new String[]{"**/*"});
        scanner.setBasedir(folderPath);
        scanner.scan();

        String[] filesFound = scanner.getIncludedFiles();
        ExecutorService executor = Executors.newFixedThreadPool(Configs.fixedPoolSize);
        String basePath = fixFolderPath(folderPath);

        for (String tempFileName : filesFound) {
            final String fileName = basePath + tempFileName;
            Runnable worker = () -> {
                Logger.Log("Parsing file " + fileName);
                String tempParsedFile = "";
                try {
                    tempParsedFile = parseFile(new File(fileName));
                } catch (IOException error) {
                    error.printStackTrace();
                    Logger.Log("Failed to parse file " + fileName);
                }

                final String parsedFile = tempParsedFile;

                Pair<String, String> filePair = new Pair<String, String>() {
                    @Override
                    public String getLeft() {
                        return fileName;
                    }

                    @Override
                    public String getRight() {
                        return parsedFile;
                    }

                    @Override
                    public String setValue(String s) {
                        return null;
                    }
                };

                fileList.add(filePair);
                Logger.Log("Done parsing file " + fileName);
            };
            executor.execute(worker);
        }
        executor.shutdown();
        //noinspection StatementWithEmptyBody
        while (!executor.isTerminated()) ;
        Logger.Log("Finished processing all files in folder " + folderPath);
        return fileList;
    }
}
