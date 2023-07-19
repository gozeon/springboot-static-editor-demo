package com.example.demo.playground;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.File;
import java.io.IOException;

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
        if(path.indexOf("/") == -1) {
            return "";
        } else {
            return path.substring(0, path.lastIndexOf("/"));
        }
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
}
