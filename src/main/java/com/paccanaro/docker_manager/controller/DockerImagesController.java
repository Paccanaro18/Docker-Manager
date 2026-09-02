package com.paccanaro.docker_manager.controller;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Image;
import com.paccanaro.docker_manager.service.DockerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/images")
public class DockerImagesController {

    private final DockerClient dockerClient;
    private final DockerService dockerService;


    public DockerImagesController(DockerClient dockerClient, DockerService dockerService) {
        this.dockerClient = dockerClient;
        this.dockerService = dockerService;
    }

    @GetMapping(" ")
    public List<Image> listImages() {
        return dockerService.listImages();
    }

    @GetMapping("/filter")
    public List<Image> filterImages(@RequestParam(required = false, defaultValue = "image-")  String filterName) {
        return dockerService.filterImages(filterName);
    }






}
