/*
 * Copyright (C) 2015 The Android Open Source Project
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

package com.example.android.architecture.blueprints.todoapp;

import android.content.Context;
import android.support.annotation.NonNull;

import com.example.android.architecture.blueprints.todoapp.addedittask.domain.usecase.DeleteTask;
import com.example.android.architecture.blueprints.todoapp.addedittask.domain.usecase.GetTask;
import com.example.android.architecture.blueprints.todoapp.addedittask.domain.usecase.SaveTask;
import com.example.android.architecture.blueprints.todoapp.data.FakeTasksRemoteDataSource;
import com.example.android.architecture.blueprints.todoapp.data.source.TasksDataSource;
import com.example.android.architecture.blueprints.todoapp.data.source.TasksRepository;
import com.example.android.architecture.blueprints.todoapp.data.source.local.TasksLocalDataSource;
import com.example.android.architecture.blueprints.todoapp.statistics.domain.usecase.GetStatistics;
import com.example.android.architecture.blueprints.todoapp.tasks.TasksFilterType;
import com.example.android.architecture.blueprints.todoapp.tasks.domain.filter.FilterFactory;
import com.example.android.architecture.blueprints.todoapp.tasks.domain.usecase.ActivateTask;
import com.example.android.architecture.blueprints.todoapp.tasks.domain.usecase.ClearCompleteTasks;
import com.example.android.architecture.blueprints.todoapp.tasks.domain.usecase.CompleteTask;
import com.example.android.architecture.blueprints.todoapp.tasks.domain.usecase.GetTasks;

import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Enables injection of mock implementations for
 * {@link TasksDataSource} at compile time. This is useful for testing, since it allows us to use
 * a fake instance of the class to isolate the dependencies and run a test hermetically.
 */
public class Injection {

   public static TasksRepository provideTasksRepository(@NonNull Context context) {
      checkNotNull(context);
      return TasksRepository.getInstance(FakeTasksRemoteDataSource.getInstance(),
            TasksLocalDataSource.getInstance(context));
   }

   public static TasksRepository provideFakeTasksRepository() {
      return TasksRepository.getInstance(FakeTasksRemoteDataSource.getInstance(),
            FakeTasksRemoteDataSource.getInstance());
   }

   public static Scheduler providePostExecutionThread() {
      return AndroidSchedulers.mainThread();
   }

   public static Scheduler provideThreadExecutor() {
      return Schedulers.io();
   }

   public static GetTasks provideGetTasks(@NonNull Context context) {
      return new GetTasks(
            Injection.provideThreadExecutor(),
            Injection.providePostExecutionThread(),
            provideTasksRepository(context), new FilterFactory(),
            false,
            TasksFilterType.ALL_TASKS);
   }

   public static GetTask provideGetTask(@NonNull Context context) {
      return new GetTask(
            Injection.provideThreadExecutor(),
            Injection.providePostExecutionThread(),
            Injection.provideTasksRepository(context));
   }

   public static SaveTask provideSaveTask(@NonNull Context context) {
      return new SaveTask(provideThreadExecutor(),providePostExecutionThread(),Injection.provideTasksRepository(context));
   }

   public static CompleteTask provideCompleteTasks(@NonNull Context context) {
      return new CompleteTask(Injection.provideThreadExecutor(),
            Injection.providePostExecutionThread(),
            Injection.provideTasksRepository(context));
   }

   public static ActivateTask provideActivateTask(@NonNull Context context) {
      return new ActivateTask(provideThreadExecutor(),providePostExecutionThread(),Injection.provideTasksRepository(context));
   }

   public static ClearCompleteTasks provideClearCompleteTasks(@NonNull Context context) {
      return new ClearCompleteTasks(provideThreadExecutor(),providePostExecutionThread(),provideTasksRepository(context));
   }

   public static DeleteTask provideDeleteTask(@NonNull Context context) {
      return new DeleteTask(provideThreadExecutor(),providePostExecutionThread(),Injection.provideTasksRepository(context));
   }

   public static GetStatistics provideGetStatistics(@NonNull Context context) {
      return new GetStatistics(provideThreadExecutor(),providePostExecutionThread(),Injection.provideTasksRepository(context));
   }
}
