package com.paccanaro.docker_manager.service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.ListContainersCmd;
import com.github.dockerjava.api.command.ListImagesCmd;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Image;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do DockerService")
class DockerServiceTest {

    @Mock
    private DockerClient dockerClient;

    @InjectMocks
    private DockerService dockerService;

    private Container containerMock;
    private Image imageMock;
    private ListContainersCmd listContainersCmdMock;
    private ListImagesCmd listImagesCmdMock;

    @BeforeEach
    void setUp() {
        containerMock = mock(Container.class);
        imageMock = mock(Image.class);
        listContainersCmdMock = mock(ListContainersCmd.class);
        listImagesCmdMock = mock(ListImagesCmd.class);
    }

    @Test
    @DisplayName("Deve listar todos os containers com sucesso")
    void testListContainers_Success() {
        when(dockerClient.listContainersCmd()).thenReturn(listContainersCmdMock);
        when(listContainersCmdMock.withShowAll(true)).thenReturn(listContainersCmdMock);
        when(listContainersCmdMock.exec()).thenReturn(Arrays.asList(containerMock));

        List<Container> resultado = dockerService.listContainers(true);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(dockerClient, times(1)).listContainersCmd();
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não há containers")
    void testListContainers_Empty() {
        when(dockerClient.listContainersCmd()).thenReturn(listContainersCmdMock);
        when(listContainersCmdMock.withShowAll(true)).thenReturn(listContainersCmdMock);
        when(listContainersCmdMock.exec()).thenReturn(Arrays.asList());

        List<Container> resultado = dockerService.listContainers(true);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("Deve iniciar um container com sucesso")
    void testStartContainer_Success() {
        String containerId = "abc123";
        var startCmdMock = mock(com.github.dockerjava.api.command.StartContainerCmd.class);
        when(dockerClient.startContainerCmd(containerId)).thenReturn(startCmdMock);

        assertDoesNotThrow(() -> dockerService.startContainer(containerId));
        verify(dockerClient, times(1)).startContainerCmd(containerId);
        verify(startCmdMock, times(1)).exec();
    }

    @Test
    @DisplayName("Não deve falhar ao iniciar container inexistente")
    void testStartContainer_NonExistent() {
        String containerId = "nao-existe";
        var startCmdMock = mock(com.github.dockerjava.api.command.StartContainerCmd.class);
        when(dockerClient.startContainerCmd(containerId)).thenReturn(startCmdMock);

        assertDoesNotThrow(() -> dockerService.startContainer(containerId));
        verify(startCmdMock, times(1)).exec();
    }

    @Test
    @DisplayName("Deve parar um container com sucesso")
    void testStopContainer_Success() {
        String containerId = "abc123";
        var stopCmdMock = mock(com.github.dockerjava.api.command.StopContainerCmd.class);
        when(dockerClient.stopContainerCmd(containerId)).thenReturn(stopCmdMock);

        assertDoesNotThrow(() -> dockerService.stopContainer(containerId));
        verify(dockerClient, times(1)).stopContainerCmd(containerId);
        verify(stopCmdMock, times(1)).exec();
    }

    @Test
    @DisplayName("Deve remover um container com sucesso")
    void testDeleteContainer_Success() {
        String containerId = "abc123";
        var removeCmdMock = mock(com.github.dockerjava.api.command.RemoveContainerCmd.class);
        when(dockerClient.removeContainerCmd(containerId)).thenReturn(removeCmdMock);

        assertDoesNotThrow(() -> dockerService.DeleteContainer(containerId));
        verify(dockerClient, times(1)).removeContainerCmd(containerId);
        verify(removeCmdMock, times(1)).exec();
    }

    @Test
    @DisplayName("Deve criar um novo container com sucesso")
    void testCreateContainer_Success() {
        String imageName = "nginx:latest";
        CreateContainerResponse responseMock = mock(CreateContainerResponse.class);
        var createCmdMock = mock(com.github.dockerjava.api.command.CreateContainerCmd.class);

        when(dockerClient.createContainerCmd(imageName)).thenReturn(createCmdMock);
        when(createCmdMock.exec()).thenReturn(responseMock);

        assertDoesNotThrow(() -> dockerService.createContainer(imageName));
        verify(dockerClient, times(1)).createContainerCmd(imageName);
        verify(createCmdMock, times(1)).exec();
    }

    @Test
    @DisplayName("Deve listar todas as imagens com sucesso")
    void testListImages_Success() {
        when(dockerClient.listImagesCmd()).thenReturn(listImagesCmdMock);
        when(listImagesCmdMock.exec()).thenReturn(Arrays.asList(imageMock));

        List<Image> resultado = dockerService.listImages();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(dockerClient, times(1)).listImagesCmd();
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não há imagens")
    void testListImages_Empty() {
        when(dockerClient.listImagesCmd()).thenReturn(listImagesCmdMock);
        when(listImagesCmdMock.exec()).thenReturn(Arrays.asList());

        List<Image> resultado = dockerService.listImages();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("Deve filtrar imagens por nome com sucesso")
    void testFilterImages_Success() {
        String filterName = "nginx";
        Image imagemNginx = mock(Image.class);

        when(dockerClient.listImagesCmd()).thenReturn(listImagesCmdMock);
        when(listImagesCmdMock.withImageNameFilter(filterName)).thenReturn(listImagesCmdMock);
        when(listImagesCmdMock.exec()).thenReturn(Arrays.asList(imagemNginx));

        List<Image> resultado = dockerService.filterImages(filterName);

        assertNotNull(resultado);
        verify(dockerClient, times(1)).listImagesCmd();
    }

    @Test
    @DisplayName("Deve retornar lista vazia ao filtrar com termo não encontrado")
    void testFilterImages_NoMatch() {
        String filterName = "nao-existe";

        when(dockerClient.listImagesCmd()).thenReturn(listImagesCmdMock);
        when(listImagesCmdMock.withImageNameFilter(filterName)).thenReturn(listImagesCmdMock);
        when(listImagesCmdMock.exec()).thenReturn(Arrays.asList());

        List<Image> resultado = dockerService.filterImages(filterName);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }
}