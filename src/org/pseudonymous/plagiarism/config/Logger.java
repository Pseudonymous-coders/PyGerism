package org.pseudonymous.plagiarism.config;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by pseudonymous
 */

public class Logger {

    public static class TeePrintStream extends PrintStream {

        private PrintStream systemOut;

        TeePrintStream(OutputStream file, PrintStream second) throws FileNotFoundException {
            super(file);
            systemOut = second;
        }

        @Override
        public void close() {
            super.close();
        }

        @Override
        public void flush() {
            super.flush();
            systemOut.flush();
        }

        @Override
        public void write(@NotNull byte[] buf, int off, int len) {
            super.write(buf, off, len);
            systemOut.write(buf, off, len);
        }

        @Override
        public void write(int b) {
            super.write(b);
            systemOut.write(b);
        }

        @Override
        public void write(@NotNull byte[] b) throws IOException {
            super.write(b);
            systemOut.write(b);
        }
    }

    private static DateFormat date_format;
    private static final String logTag = "PLAGIARISM";

    public static void init() {
        System.out.println("Starting the PlagiarismChecker Logger");

        date_format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:SSS");

        final DateFormat fileDateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm");
        final String logFilePath = Configs.logLocation + fileDateFormat.format(new Date()) + Configs.logFile;
        final File file = new File(logFilePath);
        Logger.Log("Attempting to tee output stream to " + logFilePath);

        //Create a new log file if it doesn't exist
        if (!file.exists()) {
            try {
                //noinspection ResultOfMethodCallIgnored
                file.createNewFile();
            } catch (IOException err) {
                err.printStackTrace();
                Logger.Log("Failed creating a new log file");
            }
        }

        try {
            FileOutputStream teeOut = new FileOutputStream(file);
            TeePrintStream teePrintStream = new TeePrintStream(teeOut, System.out);
            System.setOut(teePrintStream);
            System.setErr(teePrintStream);
        } catch (IOException err) {
            err.printStackTrace();
            Logger.Log("Failed creating the file tee logger output stream!");
        }
    }

    private static String getDate() {
        return date_format.format(new Date());
    }

    public static void Log(String toLog) {
        Log(toLog, false);
    }

    public static void Log(String toLog, boolean error) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append(getDate());
        sb.append("]|");
        sb.append(logTag);
        sb.append("|: ");

        if (error) {
            sb.append("(ERROR) -> ");
        }

        sb.append(toLog);

        System.out.println(sb.toString());
    }
}
