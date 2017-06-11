package ru.urfu.taskmanager.toolsTest.network;

import java.util.Collection;

import ru.urfu.taskmanager.network.APIRequestInterface;
import ru.urfu.taskmanager.network.NotesBackendService;

final class MockAPIService<T> implements NotesBackendService<T>
{
    private MockServer<T> mockedServer;
    private NotesBackendService<T> mockedService;

    private MockAPIService(MockServer<T> server, NotesBackendService<T> service) {
        mockedServer = server;
        mockedService = service;
    }

    @Override
    public APIRequestInterface<Collection<T>> getUserNotes() {
        return new MockAPIRequest<>(mockedServer, mockedService.getUserNotes());
    }

    @Override
    public APIRequestInterface<T> getNoteById(int noteId) {
        return new MockAPIRequest<>(mockedServer, mockedService.getNoteById(noteId));
    }

    @Override
    public APIRequestInterface<Integer> createNote(T entry) {
        return new MockAPIRequest<>(mockedServer, mockedService.createNote(entry));
    }

    @Override
    public APIRequestInterface<Void> editNote(T entry, int id) {
        return new MockAPIRequest<>(mockedServer, mockedService.editNote(entry, id));
    }

    @Override
    public APIRequestInterface<Void> deleteNote(int id) {
        return new MockAPIRequest<>(mockedServer, mockedService.deleteNote(id));
    }

    static <T> MockAPIService<T> mock(MockServer<T> server, NotesBackendService<T> service) {
        return new MockAPIService<>(server, service);
    }
}
