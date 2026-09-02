package com.paccanaro.docker_manager.controller;

import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Image;
import com.paccanaro.docker_manager.service.DockerService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/containers")
public class DockerContainersController {

    private final DockerService dockerService;


    public DockerContainersController(DockerService dockerService) {
        this.dockerService = dockerService;
    }

    @GetMapping
    public List<Container> listContainer(@RequestParam(required = false, defaultValue = "true")  boolean all) {
        return dockerService.listContainers(all);
    }

    @PostMapping("/{id}/start")
    public void startContainer(@PathVariable String id) {
        dockerService.startContainer(id);
    }

    @PostMapping("/{id}/stop")
    public void stopContainer(@PathVariable String id) {
        dockerService.stopContainer(id);
    }

    @DeleteMapping("/{id}")
    public void deleteContainer(@PathVariable String id) {
        dockerService.DeleteContainer(id);
    }

    @PostMapping("")
    public void createContainer(@RequestParam String imageName) {
        dockerService.createContainer(imageName);
    }



}
