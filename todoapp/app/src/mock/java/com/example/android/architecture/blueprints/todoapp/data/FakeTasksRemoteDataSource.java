/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.architecture.blueprints.todoapp.data;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.example.android.architecture.blueprints.todoapp.data.source.TasksDataSource;
import com.example.android.architecture.blueprints.todoapp.tasks.domain.model.Task;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import rx.Completable;
import rx.CompletableSubscriber;
import rx.Observable;

/**
 * Implementation of a remote data source with static access to the data for easy testing.
 */
public class FakeTasksRemoteDataSource implements TasksDataSource {

    private static FakeTasksRemoteDataSource INSTANCE;

    private static final Map<String, Task> TASKS_SERVICE_DATA = new LinkedHashMap<>();

    // Prevent direct instantiation.
    private FakeTasksRemoteDataSource() {}

    public static FakeTasksRemoteDataSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FakeTasksRemoteDataSource();
        }
        return INSTANCE;
    }

    @Override
    public Observable<ArrayList<Task>> getTasks() {
        return Observable.just(Lists.newArrayList(TASKS_SERVICE_DATA.values()));
    }

    @Override
    public Observable<Task> getTask(@NonNull String taskId) {
        return Observable.just(TASKS_SERVICE_DATA.get(taskId));
    }

    @Override
    public void saveTask(@NonNull Task task) {
        TASKS_SERVICE_DATA.put(task.getId(), task);
    }

    @Override
    public Completable completeTask(@NonNull final Task task) {

       return Completable.create(new Completable.OnSubscribe() {
            @Override
            public void call(CompletableSubscriber completableSubscriber) {
                Task completedTask = new Task(task.getTitle(), task.getDescription(), task.getId(), true);
                TASKS_SERVICE_DATA.put(task.getId(), completedTask);
                completableSubscriber.onCompleted();
            }
        });
    }

    @Override
    public Completable completeTask(@NonNull String taskId) {
        // Not required for the remote data source.
        return null;
    }

    @Override
    public Completable activateTask(@NonNull final Task task) {
        return Completable.create(new Completable.OnSubscribe() {
            @Override
            public void call(CompletableSubscriber completableSubscriber) {
                Task activeTask = new Task(task.getTitle(), task.getDescription(), task.getId());
                TASKS_SERVICE_DATA.put(task.getId(), activeTask);
                completableSubscriber.onCompleted();
            }
        });
    }

    @Override
    public Completable activateTask(@NonNull String taskId) {
        // Not required for the remote data source.
        return null;
    }

    @Override
    public Completable clearCompletedTasks() {
        return Completable.create(new Completable.OnSubscribe() {
            @Override
            public void call(CompletableSubscriber completableSubscriber) {
                Iterator<Map.Entry<String, Task>> it = TASKS_SERVICE_DATA.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<String, Task> entry = it.next();
                    if (entry.getValue().isCompleted()) {
                        it.remove();
                    }
                }
            }
        });

    }

    public void refreshTasks() {
        // Not required because the {@link TasksRepository} handles the logic of refreshing the
        // tasks from all the available data sources.
    }

    @Override
    public Completable deleteTask(@NonNull final String taskId) {
       return Completable.create(new Completable.OnSubscribe() {
            @Override
            public void call(CompletableSubscriber completableSubscriber) {
                TASKS_SERVICE_DATA.remove(taskId);
                completableSubscriber.onCompleted();
            }
        });

    }

    @Override
    public void deleteAllTasks() {
        TASKS_SERVICE_DATA.clear();
    }

    @VisibleForTesting
    public void addTasks(Task... tasks) {
        for (Task task : tasks) {
            TASKS_SERVICE_DATA.put(task.getId(), task);
        }
    }
}
