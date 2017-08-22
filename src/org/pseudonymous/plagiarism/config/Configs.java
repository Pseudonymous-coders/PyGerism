package org.pseudonymous.plagiarism.config;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.pseudonymous.plagiarism.components.Dialogs;

import java.io.*;

/**
 * Created by pseudonymous
 */
public class Configs {
    /*
     * General/Window configurations
     */
    public static final String appName = "PlagiarismChecker";
    public static final int
            windowX = 1280,
            windowY = 720;
    public static final String logLocation = "logs/";
    public static final String logFile = "plagiarism-checker-log.txt";
    public static final String iconName = "logo";

    /*
     * Processing configurations
     */
    public static int sentenceMinSize = 10; //Minimum character count for a sentence
    public static int sentenceMinWords = 5; //Minimum word count for a sentence
    public static int fixedPoolSize = 50; //Allow for 50 threads to run at once
    public static double ratioMin = 0.6; //80 percent match for each sentence
    public static double ratioParentMin = 0.95; //90 percent match for each parent sentence
    public static int minCounts = 1; //Minimum amount of flags to be considered plagiarised

    public static final String load_config = "config.json";
    private static JSONObject loaded_config = null;

    public static void init() throws IOException {
        Logger.Log("Loading config from " + load_config);
        try {
            File config_file = new File(load_config);
            if (!config_file.isFile() || !config_file.exists()) {
                if (!config_file.createNewFile()) {
                    Dialogs.ErrorDialog(Configs.appName + " | Failed loading configurations",
                            "Failed to load the configuration files!");
                }
                saveConfigurations();
            }
            InputStream in = new FileInputStream(config_file);
            loaded_config = new JSONObject(new JSONTokener(in));
            in.close();
        } catch (IOException | JSONException err) {
            err.printStackTrace();
            Logger.Log("Failed loading configurations!", true);
            throw new IOException("Failed loading the config file!");
        }
        Logger.Log("Finished loading configurations!");

        try {
            sentenceMinSize = loaded_config.getInt("sentenceMinSize");
            sentenceMinWords = loaded_config.getInt("sentenceMinWords");
            ratioMin = loaded_config.getDouble("ratioMin");
            ratioParentMin = loaded_config.getDouble("ratioParentMin");
            minCounts = loaded_config.getInt("minCounts");
        } catch (Throwable err) {
            err.printStackTrace();
            Logger.Log("Failed parsing configuration file", true);
            throw new IOException("Failed to parse configuration file");
        }
    }

    public static void saveConfigurations() {
        Logger.Log("Saving configurations");
        JSONObject toWrite = new JSONObject();
        toWrite.put("sentenceMinSize", sentenceMinSize);
        toWrite.put("sentenceMinWords", sentenceMinWords);
        toWrite.put("ratioMin", ratioMin);
        toWrite.put("ratioParentMin", ratioParentMin);
        toWrite.put("minCounts", minCounts);

        PrintWriter out;
        try {
            out = new PrintWriter(new FileOutputStream(new File(load_config)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Logger.Log("Failed to save configuration settings");
            Dialogs.ErrorDialog(appName + " | Failed saving", "Failed saving settings");
            return;
        }

        out.println(toWrite.toString());
        out.close();
    }
}
