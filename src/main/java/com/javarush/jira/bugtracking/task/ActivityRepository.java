package com.javarush.jira.bugtracking.task;

import com.javarush.jira.common.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface ActivityRepository extends BaseRepository<Activity> {
    @Query("SELECT a FROM Activity a JOIN FETCH a.author WHERE a.taskId =:taskId ORDER BY a.updated DESC")
    List<Activity> findAllByTaskIdOrderByUpdatedDesc(long taskId);

    @Query("SELECT a FROM Activity a JOIN FETCH a.author WHERE a.taskId =:taskId AND a.comment IS NOT NULL ORDER BY a.updated DESC")
    List<Activity> findAllComments(long taskId);

    @Query("SELECT a FROM Activity a WHERE a.taskId=:taskId and a.statusCode='in_progress' or a.statusCode='ready_for_review' ORDER BY a.updated DESC")
    List<Activity> findStatusInProgress(long taskId);

    @Query("SELECT a FROM Activity a WHERE a.taskId=:taskId and a.statusCode='done' or a.statusCode='ready_for_review' ORDER BY a.updated DESC")
    List<Activity> findStatusIsDone(long taskId);
}
