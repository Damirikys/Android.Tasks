package ru.urfu.taskmanager.network;

import java.util.Collection;

public interface NotesBackendService<T>
{
    APIRequestInterface<Collection<T>> getUserNotes();

    APIRequestInterface<T> getNoteById(int noteId);

    APIRequestInterface<Integer> createNote(T entry);

    APIRequestInterface<Void> editNote(T entry, int id);

    APIRequestInterface<Void> deleteNote(int id);
}
