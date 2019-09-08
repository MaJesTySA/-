package com.inside.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class MergeVideoBgm {
    private String ffmpegEXE;

    public MergeVideoBgm(String ffmpegEXE) {
        this.ffmpegEXE = ffmpegEXE;
    }

    public String removeVideoSound(String videoInputPath) throws IOException {
        List<String> command = new ArrayList<>();
        String tempFile = videoInputPath.substring(0, videoInputPath.lastIndexOf('/')) + "/tempFile.mp4";
        command.add(ffmpegEXE);
        command.add("-i");
        command.add(videoInputPath);
        command.add("-vcodec");
        command.add("copy");
        command.add("-an");
        command.add(tempFile);
        processCommand(command);
        return tempFile;
    }

    public void convert(String videoInputPath, String bgmInputPath, double seconds, String outputPath) throws IOException {
        List<String> command = new ArrayList<>();
        command.add(ffmpegEXE);
        command.add("-i");
        //互换位置
        command.add(videoInputPath);
        command.add("-i");
        command.add(bgmInputPath);
        command.add("-t");
        command.add(String.valueOf(seconds));
        // command.add("-y");
        command.add(outputPath);
        processCommand(command);
    }

    public void getCover(String videoInputPath, String coverOutputPath) throws IOException {
        List<String> command = new ArrayList<>();
        command.add(ffmpegEXE);
        command.add("-ss");
        command.add("00:00:01");
        command.add("-y");
        command.add("-i");
        command.add(videoInputPath);
        command.add("-vframes");
        command.add("1");
        command.add(coverOutputPath);
        processCommand(command);
    }

    private void processCommand(List<String> command) throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        Process process = processBuilder.start();
        InputStream errorStream = process.getErrorStream();
        InputStreamReader inputStreamReader = new InputStreamReader(errorStream);
        BufferedReader br = new BufferedReader(inputStreamReader);
        String line = "";
        while ((line = br.readLine()) != null) {
        }
        if (br != null) br.close();
        if (inputStreamReader != null) inputStreamReader.close();
        if (errorStream != null) errorStream.close();
    }

}
