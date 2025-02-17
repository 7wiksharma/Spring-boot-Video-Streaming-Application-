package com.stream.app.spring_stream_backend.services.impl;

import com.stream.app.spring_stream_backend.entities.Video;
import com.stream.app.spring_stream_backend.repository.VideoRepository;
import com.stream.app.spring_stream_backend.services.VideoService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Service
public class VideoServiceImpl implements VideoService {
@Value("${files.video}")
    String  DIR;

@Value("${file.video.hsl}")
String HSL_DIR;
    private VideoRepository videoRepository;
    public VideoServiceImpl(VideoRepository videoRepository) {
        this.videoRepository = videoRepository;
    }



@PostConstruct
public void init()
{
    File file = new File(DIR);
    try {
        Files.createDirectories(Paths.get(HSL_DIR));
    } catch (IOException e) {
        throw new RuntimeException(e);
    }
    if(!file.exists()){
        file.mkdir();
        System.out.println("Folder is Created");

    }
    else {
        System.out.println("Folder Already Created");

    }


}
    @Override
    public Video save(Video video, MultipartFile file) {
        try {
            String filename = file.getOriginalFilename();
            String contentType = file.getContentType();
            InputStream inputStream = file.getInputStream();

            //folder  path : create
         String cleanFileName =   StringUtils.cleanPath(filename);
         String cleanFolder = StringUtils.cleanPath(DIR);
         Path path= Paths.get(cleanFolder,cleanFileName);
         System.out.println(contentType);
         System.out.println(path);
            Files.copy(inputStream,path,StandardCopyOption.REPLACE_EXISTING);
            video.setContentType(contentType);
            video.setFilePath(path.toString());
            Video savedVideo = videoRepository.save(video);
            processVideo(savedVideo.getVideoId());
            //saving meta data
            return savedVideo;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public Video getById(String videoId) {
        Video video = videoRepository.findById(videoId).orElseThrow(() -> new RuntimeException("video not found"));

        return video;
    }

    @Override
    public Video getByTitle(String title) {
        return null;
    }

    @Override
    public List<Video> getAll() {
        return videoRepository.findAll();
    }

    @Override
    public String processVideo(String videoId) {



        Video video = this.getById(videoId);
        String filePath = video.getFilePath();
        Path videoPath = Paths.get(filePath);
//        String output360p = HSL_DIR+videoId+"/360p/";
//        String output720p = HSL_DIR+videoId+"/720p/";
//        String output1080p = HSL_DIR+videoId+"/1080p/";
        Path outputPath = Paths.get(HSL_DIR, videoId);

        try {
//            Files.createDirectories(Paths.get(output360p));
//            Files.createDirectories(Paths.get(output720p));
//            Files.createDirectories(Paths.get(output1080p));


            Files.createDirectories(outputPath);
            String formattedVideoPath = videoPath.toAbsolutePath().toString().replace("\\", "/");
            String formattedOutputPath = outputPath.toAbsolutePath().toString().replace("\\", "/");

            String[] command = {
                    "C:\\ffmpeg\\ffmpeg-7.1-essentials_build\\bin\\ffmpeg.exe",
                    "-i", formattedVideoPath,
                    "-c:v", "libx264",
                    "-c:a", "aac",
                    "-strict", "-2",
                    "-f", "hls",
                    "-hls_time", "10",
                    "-hls_list_size", "0",
                    "-hls_segment_filename", formattedOutputPath + "/segment_%3d.ts",
                    formattedOutputPath + "/master.m3u8"
            };




//            StringBuilder ffmpegCmd = new StringBuilder();
//          ffmpegCmd.append("ffmpeg  -i ")
//                    .append(videoPath.toString())
//                  .append(" -c:v libx264 -c:a aac")
//                    .append(" ")
//                    .append("-map 0:v -map 0:a -s:v:0 640x360 -b:v:0 800k ")
//                 .append("-map 0:v -map 0:a -s:v:1 1280x720 -b:v:1 2800k ")
//                 .append("-map 0:v -map 0:a -s:v:2 1920x1080 -b:v:2 5000k ")
//                  .append("-var_stream_map \"v:0,a:0 v:1,a:0 v:2,a:0\" ")
//                   .append("-master_pl_name ").append(HSL_DIR).append(videoId).append("/master.m3u8 ")
//                 .append("-f hls -hls_time 10 -hls_list_size 0 ")
//                 .append("-hls_segment_filename \"").append(HSL_DIR).append(videoId).append("/v%v/fileSequence%d.ts\" ")
//                  .append("\"").append(HSL_DIR).append(videoId).append("/v%v/prog_index.m3u8\"");

           // System.out.println(ffmpegCmd);


            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.inheritIO(); // This ensures that the output is visible in the console
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("Video processing failed with exit code: " + exitCode);
            }


            return videoId;

        } catch (IOException | InterruptedException ex) {
            throw new RuntimeException("Video processing failed: " + ex.getMessage(), ex);
        }


    }
}
