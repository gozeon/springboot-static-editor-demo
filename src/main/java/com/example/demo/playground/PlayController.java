package com.example.demo.playground;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("play")
public class PlayController {

    private ResourceLoader resourceLoader;

    public PlayController(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    private Resource[] getResources(String directoryPath) throws IOException {
        return ResourcePatternUtils.getResourcePatternResolver(resourceLoader).getResources(directoryPath);
    }

    private String getCurrentPath(String path) {
        if (path == null) {
            return "classpath:/*";
        } else {
            return "classpath:" + path + "/*";
        }
    }

    private String getParentPath(String path) {
        if (path.indexOf("/") == -1) {
            return "";
        } else {
            return path.substring(0, path.lastIndexOf("/"));
        }
    }

    private ResponseEntity<Object> generateResponse(String message, HttpStatus status, Object responseObj) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("message", message);
        map.put("status", status.value());
        map.put("data", responseObj);

        return new ResponseEntity<Object>(map, status);
    }

    @GetMapping("")
    public String index(@RequestParam(name = "path", required = false) String path, Model model)
            throws IOException {

        Boolean isRoot = (path == null || path == "");
        String formatPath = isRoot ? null : path.replaceAll("/+$", "");
        Resource[] resources = this.getResources(this.getCurrentPath(formatPath));

        model.addAttribute("isRoot", isRoot);
        model.addAttribute("parentPath", isRoot ? "" : this.getParentPath(formatPath));
        model.addAttribute("basepath", isRoot ? "" : formatPath + "/");
        model.addAttribute("fileList", resources);
        return "play/index";
    }

    @GetMapping("file")
    @ResponseBody
    public ResponseEntity<Object> getFileInfo(@RequestParam(name = "path") String path)
            throws IOException {
        Resource resource = new ClassPathResource(path);
        if (!resource.exists()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        File file = resource.getFile();
        Path filepath = file.toPath();
        String content = new String(Files.readAllBytes(filepath));
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("path", path);
        map.put("content", content);
        map.put("mimeType", Files.probeContentType(filepath));
        map.put("extension", path.substring(path.lastIndexOf(".")));

        return this.generateResponse("ok", HttpStatus.OK, map);
    }
}
